package com.sistemaventas.backend.infrastructure.mercadopago;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.resources.preference.Preference;
import com.sistemaventas.backend.infrastructure.web.dto.mercadopago.PreferenceDTO;
import com.sistemaventas.backend.infrastructure.web.dto.mercadopago.PreferenceItemDTO;

import jakarta.annotation.PostConstruct;

@Service
public class MercadoPagoService {
    private static final Logger logger = LoggerFactory.getLogger(MercadoPagoService.class);

    @Value("${mercadopago.access.token}")
    private String accessToken;

    @Value("${mercadopago.notification.url}")
    private String notificationUrl;

    @PostConstruct
    public void initialize() {
        MercadoPagoConfig.setAccessToken(accessToken);
    }

    public String createPreference(PreferenceDTO preferenceDTO) {
        try {
            logger.info("Creating payment preference for external reference: {}",
                       preferenceDTO.getExternalReference());
            logger.info("Items count: {}", preferenceDTO.getItems().size());
            for (PreferenceItemDTO item : preferenceDTO.getItems()) {
                logger.info("Item: title={}, quantity={}, unitPrice={}, currencyId={}",
                    item.getTitle(), item.getQuantity(), item.getUnitPrice(), item.getCurrencyId());
            }
            if (preferenceDTO.getBackUrls() != null) {
                logger.info("Back URLs - success: {}, pending: {}, failure: {}",
                    preferenceDTO.getBackUrls().getSuccess(),
                    preferenceDTO.getBackUrls().getPending(),
                    preferenceDTO.getBackUrls().getFailure());
            } else {
                logger.warn("Back URLs are NULL!");
            }

            PreferenceClient client = new PreferenceClient();
            List<PreferenceItemRequest> items = preferenceDTO.getItems().stream()
                .map(this::convertToPreferenceItem)
                .collect(Collectors.toList());

            BigDecimal totalCalculado = preferenceDTO.getItems().stream()
                .map(item -> item.getUnitPrice().multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            logger.info("Total calculado: ${}", totalCalculado);

            var builder = PreferenceRequest.builder()
                .items(items)
                .externalReference(preferenceDTO.getExternalReference())
                .statementDescriptor("POS Ventas")
                .binaryMode(false);

            if (preferenceDTO.getBackUrls() != null &&
                preferenceDTO.getBackUrls().getSuccess() != null) {
                builder.backUrls(buildBackUrls(preferenceDTO));
            }

            Preference preference = client.create(builder.build());
            logger.info("Payment preference created: {}", preference.getId());
            return preference.getId();
        } catch (com.mercadopago.exceptions.MPApiException mpException) {
            logger.error("MercadoPago API Error [{}]: {}", mpException.getStatusCode(), mpException.getMessage());
            String errorMsg = "Error de Mercado Pago [" + mpException.getStatusCode() + "]: " + mpException.getMessage();
            if (mpException.getApiResponse() != null) {
                errorMsg += " - Response: " + mpException.getApiResponse().getContent();
            }
            throw new RuntimeException(errorMsg, mpException);
        } catch (Exception e) {
            logger.error("Error creating MercadoPago preference: {}", e.getMessage(), e);
            throw new RuntimeException("Error general: " + e.getMessage(), e);
        }
    }

    private PreferenceItemRequest convertToPreferenceItem(PreferenceItemDTO item) {
        return PreferenceItemRequest.builder()
            .title(item.getTitle())
            .quantity(item.getQuantity())
            .unitPrice(item.getUnitPrice())
            .currencyId(item.getCurrencyId() != null ? item.getCurrencyId() : "COP")
            .description(item.getDescription())
            .pictureUrl(item.getPictureUrl())
            .categoryId(item.getCategoryId())
            .build();
    }

    private PreferenceBackUrlsRequest buildBackUrls(PreferenceDTO preferenceDTO) {
        return PreferenceBackUrlsRequest.builder()
            .success(preferenceDTO.getBackUrls().getSuccess())
            .pending(preferenceDTO.getBackUrls().getPending())
            .failure(preferenceDTO.getBackUrls().getFailure())
            .build();
    }

    public void handleNotification(String notificationType, String notificationId) {
        // Integración de notificaciones MercadoPago
    }
}
