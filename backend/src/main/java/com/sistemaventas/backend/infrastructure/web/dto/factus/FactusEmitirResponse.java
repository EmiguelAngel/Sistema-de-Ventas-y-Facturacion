package com.sistemaventas.backend.infrastructure.web.dto.factus;

public record FactusEmitirResponse(
        boolean exito,
        String cufe,
        String numeroFactura,
        String mensaje,
        String qrUrl
) {}
