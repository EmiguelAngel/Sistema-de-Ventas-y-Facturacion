package com.sistemaventas.backend.infrastructure.web.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DevolucionDTO {

    private Long idDevolucion;
    private Integer idFactura;
    private String numeroFactura;
    private String paymentId;
    private String refundId;
    private BigDecimal montoDevuelto;
    private String motivo;
    private String estado;
    private LocalDateTime fechaDevolucion;
    private String usuarioDevolucion;
}
