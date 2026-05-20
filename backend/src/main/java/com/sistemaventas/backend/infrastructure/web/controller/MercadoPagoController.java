package com.sistemaventas.backend.infrastructure.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sistemaventas.backend.infrastructure.web.dto.mercadopago.PreferenceDTO;
import com.sistemaventas.backend.infrastructure.web.dto.mercadopago.PreferenceItemDTO;
import com.sistemaventas.backend.infrastructure.mercadopago.MercadoPagoService;

@RestController
@RequestMapping("/api/payments")
public class MercadoPagoController {

    private static final Logger logger = LoggerFactory.getLogger(MercadoPagoController.class);

    private final MercadoPagoService mercadoPagoService;

    public MercadoPagoController(MercadoPagoService mercadoPagoService) {
        this.mercadoPagoService = mercadoPagoService;
    }

    @PostMapping("/create-preference")
    public ResponseEntity<?> createPreference(@RequestBody PreferenceDTO request) {
        try {
            logger.info("=== MERCADOPAGO REQUEST ===");
            logger.info("Items count: {}", request.getItems() != null ? request.getItems().size() : "null");
            logger.info("External reference: {}", request.getExternalReference());

            if (request.getItems() == null || request.getItems().isEmpty()) {
                return ResponseEntity.badRequest().body(new ErrorResponse("Items no puede estar vacío"));
            }

            for (int i = 0; i < request.getItems().size(); i++) {
                var item = request.getItems().get(i);
                if (item.getTitle() == null || item.getTitle().isEmpty()) {
                    return ResponseEntity.badRequest().body(new ErrorResponse("Item " + i + ": title es requerido"));
                }
                if (item.getQuantity() == null || item.getQuantity() <= 0) {
                    return ResponseEntity.badRequest().body(new ErrorResponse("Item " + i + ": quantity debe ser mayor a 0"));
                }
                if (item.getUnitPrice() == null || item.getUnitPrice().doubleValue() <= 0) {
                    return ResponseEntity.badRequest().body(new ErrorResponse("Item " + i + ": unitPrice debe ser mayor a 0"));
                }
            }

            String preferenceId = mercadoPagoService.createPreference(request);
            logger.info("Preference created: {}", preferenceId);
            return ResponseEntity.ok(new PreferenceResponse(preferenceId));
        } catch (Exception e) {
            logger.error("Error creating MercadoPago preference: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ErrorResponse("Error: " + e.getMessage()));
        }
    }

    @PostMapping("/test-credentials")
    public ResponseEntity<?> testCredentials() {
        logger.info("=== TESTING MERCADOPAGO CREDENTIALS ===");
        try {
            var testDTO = new PreferenceDTO();
            var item = new PreferenceItemDTO();
            item.setTitle("Test Product");
            item.setQuantity(1);
            item.setUnitPrice(new java.math.BigDecimal("1000"));
            item.setCurrencyId("COP");
            item.setDescription("Test");
            testDTO.setItems(java.util.Arrays.asList(item));
            testDTO.setExternalReference("TEST-" + System.currentTimeMillis());
            var backUrls = new PreferenceDTO.BackUrlsDTO();
            backUrls.setSuccess("https://example.com/success");
            backUrls.setPending("https://example.com/pending");
            backUrls.setFailure("https://example.com/failure");
            testDTO.setBackUrls(backUrls);

            String preferenceId = mercadoPagoService.createPreference(testDTO);
            return ResponseEntity.ok().body(java.util.Map.of(
                "status", "success",
                "message", "Credenciales válidas",
                "preferenceId", preferenceId
            ));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(java.util.Map.of(
                "status", "error",
                "message", e.getMessage() != null ? e.getMessage() : "Unknown error",
                "type", e.getClass().getSimpleName()
            ));
        }
    }

    @PostMapping("/notifications")
    public ResponseEntity<?> handleNotification(
            @RequestParam("type") String type,
            @RequestParam("data.id") String id) {
        mercadoPagoService.handleNotification(type, id);
        return ResponseEntity.ok().build();
    }

    @SuppressWarnings("unused")
    private static class ErrorResponse {
        private String message;
        public ErrorResponse(String message) { this.message = message; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    @SuppressWarnings("unused")
    private static class PreferenceResponse {
        private String preferenceId;
        public PreferenceResponse(String preferenceId) { this.preferenceId = preferenceId; }
        public String getPreferenceId() { return preferenceId; }
        public void setPreferenceId(String preferenceId) { this.preferenceId = preferenceId; }
    }
}
