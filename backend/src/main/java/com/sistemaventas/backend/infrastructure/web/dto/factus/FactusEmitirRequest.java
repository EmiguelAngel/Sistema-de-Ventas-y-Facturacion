package com.sistemaventas.backend.infrastructure.web.dto.factus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record FactusEmitirRequest(
        @NotNull Integer idFactura,
        @NotBlank String tipoDocumento,
        @NotBlank String numeroDocumento,
        @NotBlank String nombreComprador,
        String emailComprador,
        String direccion,
        String digitoVerificacion,
        String telefono
) {}
