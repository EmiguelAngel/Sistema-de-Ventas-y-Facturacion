package com.sistemaventas.backend.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * POJO legacy — sustituida por DevolucionJpaEntity en la capa de infraestructura.
 * NO es una entidad JPA; se conserva como POJO por compatibilidad.
 */
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
