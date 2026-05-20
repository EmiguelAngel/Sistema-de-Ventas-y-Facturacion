package com.sistemaventas.backend.infrastructure.web.dto.legacy;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Devolucion {

    private Long idDevolucion;
    private Factura factura;
    private String paymentId;
    private String refundId;
    private BigDecimal montoDevuelto;
    private String motivo;

    @Builder.Default
    private String estado = "PENDIENTE";

    @Builder.Default
    private LocalDateTime fechaDevolucion = LocalDateTime.now();

    private String usuarioDevolucion;
}
