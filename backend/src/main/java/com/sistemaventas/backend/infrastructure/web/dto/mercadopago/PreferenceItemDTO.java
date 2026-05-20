package com.sistemaventas.backend.infrastructure.web.dto.mercadopago;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class PreferenceItemDTO {
    private String title;
    private Integer quantity;
    private BigDecimal unitPrice;
    private String currencyId;
    private String description;
    private String pictureUrl;
    private String categoryId;
}
