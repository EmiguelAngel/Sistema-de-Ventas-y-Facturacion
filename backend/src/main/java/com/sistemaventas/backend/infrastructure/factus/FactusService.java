package com.sistemaventas.backend.infrastructure.factus;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sistemaventas.backend.infrastructure.web.dto.factus.FactusEmitirRequest;
import com.sistemaventas.backend.infrastructure.web.dto.factus.FactusEmitirResponse;
import com.sistemaventas.backend.infrastructure.persistence.entity.DetalleFacturaJpaEntity;
import com.sistemaventas.backend.infrastructure.persistence.repository.DetalleFacturaJpaRepository;
import com.sistemaventas.backend.infrastructure.persistence.repository.FacturaJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class FactusService {

    private static final Logger log = LoggerFactory.getLogger(FactusService.class);
    private static final String CREDENCIALES_NO_CONFIGURADAS = "REPLACE_WITH_CLIENT_ID";

    @Value("${factus.api.url}")
    private String apiUrl;

    @Value("${factus.client.id}")
    private String clientId;

    @Value("${factus.client.secret}")
    private String clientSecret;

    @Value("${factus.numbering.range.id:1}")
    private int numberingRangeId;

    private final FacturaJpaRepository facturaRepo;
    private final DetalleFacturaJpaRepository detalleRepo;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public FactusService(FacturaJpaRepository facturaRepo, DetalleFacturaJpaRepository detalleRepo) {
        this.facturaRepo = facturaRepo;
        this.detalleRepo = detalleRepo;
    }

    public FactusEmitirResponse emitir(FactusEmitirRequest req) {
        if (CREDENCIALES_NO_CONFIGURADAS.equals(clientId)) {
            return new FactusEmitirResponse(false, null, null,
                    "FACTUS no configurado: agrega client_id y client_secret en application.properties", null);
        }

        try {
            String token = obtenerToken();
            return crearFacturaElectronica(token, req);
        } catch (HttpClientErrorException e) {
            log.error("Error HTTP de FACTUS [{}]: {}", e.getStatusCode(), e.getResponseBodyAsString());
            return new FactusEmitirResponse(false, null, null,
                    "Error de FACTUS (" + e.getStatusCode() + "): " + extraerMensajeError(e.getResponseBodyAsString()), null);
        } catch (Exception e) {
            log.error("Error al emitir factura FACTUS para factura #{}: {}", req.idFactura(), e.getMessage(), e);
            return new FactusEmitirResponse(false, null, null, "Error: " + e.getMessage(), null);
        }
    }

    private String obtenerToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<JsonNode> response = restTemplate.postForEntity(apiUrl + "/oauth/token", entity, JsonNode.class);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new RuntimeException("Error autenticando con FACTUS");
        }
        return response.getBody().get("access_token").asText();
    }

    private FactusEmitirResponse crearFacturaElectronica(String token, FactusEmitirRequest req) {
        facturaRepo.findById(req.idFactura())
                .orElseThrow(() -> new RuntimeException("Factura no encontrada: " + req.idFactura()));

        List<DetalleFacturaJpaEntity> detalles = detalleRepo.findByFactura_IdFactura(req.idFactura());
        if (detalles.isEmpty()) {
            throw new RuntimeException("La factura #" + req.idFactura() + " no tiene items");
        }

        ObjectNode payload = construirPayload(req, detalles);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        headers.set("Accept", "application/json");

        HttpEntity<String> entity = new HttpEntity<>(payload.toString(), headers);
        ResponseEntity<JsonNode> response = restTemplate.postForEntity(
                apiUrl + "/v1/bills/validate", entity, JsonNode.class);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new RuntimeException("FACTUS rechazó la factura");
        }

        return extraerResultado(response.getBody());
    }

    private ObjectNode construirPayload(FactusEmitirRequest req, List<DetalleFacturaJpaEntity> detalles) {
        ObjectNode payload = objectMapper.createObjectNode();
        payload.put("numbering_range_id", numberingRangeId);
        payload.put("reference_code", "POS-" + req.idFactura());
        payload.put("observation", "Venta POS - Factura #" + req.idFactura());
        payload.put("payment_method_code", "10"); // Efectivo — genérico para POS

        // Datos del comprador
        ObjectNode customer = objectMapper.createObjectNode();
        customer.put("identification", req.numeroDocumento());
        customer.put("names", req.nombreComprador());
        customer.put("address", req.direccion() != null ? req.direccion() : "");
        customer.put("email", req.emailComprador() != null ? req.emailComprador() : "");
        customer.put("mobile", req.telefono() != null ? req.telefono() : "");

        // identification_document_id: 3=CC, 6=NIT según FACTUS
        boolean esNit = "31".equals(req.tipoDocumento());
        customer.put("identification_document_id", esNit ? 6 : 3);
        if (esNit && req.digitoVerificacion() != null) {
            customer.put("dv", req.digitoVerificacion());
        }
        payload.set("customer", customer);

        // Productos
        ArrayNode items = objectMapper.createArrayNode();
        for (DetalleFacturaJpaEntity detalle : detalles) {
            ObjectNode item = objectMapper.createObjectNode();
            item.put("code_reference", "PROD-" + detalle.getProducto().getIdProducto());
            item.put("name", detalle.getProducto().getDescripcion());
            item.put("quantity", detalle.getCantidad());
            item.put("discount_rate", "0.00");
            // precioUnitario en BD es sin IVA
            item.put("price", detalle.getPrecioUnitario().toPlainString());
            item.put("tax_rate", "19.00");
            item.put("unit_measure_id", 70);   // Unidad
            item.put("standard_code_id", 1);
            item.put("is_excluded", 0);
            item.put("tribute_id", 1);
            items.add(item);
        }
        payload.set("items", items);

        return payload;
    }

    private FactusEmitirResponse extraerResultado(JsonNode responseBody) {
        // FACTUS puede envolver la respuesta en "data" → "bill" o directamente
        JsonNode bill = responseBody;
        if (responseBody.has("data")) {
            bill = responseBody.get("data");
        }
        if (bill.has("bill")) {
            bill = bill.get("bill");
        }

        String cufe = bill.has("cufe") ? bill.get("cufe").asText(null) : null;
        String numero = bill.has("number") ? bill.get("number").asText(null) : null;
        String qr = bill.has("qr") ? bill.get("qr").asText(null) : null;

        log.info("Factura electrónica emitida — Número: {}, CUFE: {}", numero, cufe);
        return new FactusEmitirResponse(true, cufe, numero, "Factura electrónica emitida exitosamente", qr);
    }

    private String extraerMensajeError(String responseBody) {
        try {
            JsonNode node = objectMapper.readTree(responseBody);
            if (node.has("message")) return node.get("message").asText();
            if (node.has("error")) return node.get("error").asText();
        } catch (Exception ignored) {}
        return responseBody;
    }
}
