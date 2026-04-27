package com.sistemaventas.backend.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Entidad JPA para la tabla devolucion. Mantiene @GeneratedValue(IDENTITY) original. */
@Entity
@Table(name = "devolucion")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DevolucionJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "iddevolucion")
    private Long idDevolucion;

    @ManyToOne
    @JoinColumn(name = "idfactura", nullable = false)
    private FacturaJpaEntity factura;

    @Column(name = "payment_id", length = 100)
    private String paymentId;

    @Column(name = "refund_id", length = 100)
    private String refundId;

    @Column(name = "monto_devuelto", nullable = false, precision = 10, scale = 2)
    private BigDecimal montoDevuelto;

    @Column(name = "motivo", length = 500)
    private String motivo;

    @Builder.Default
    @Column(name = "estado", length = 50)
    private String estado = "PENDIENTE";

    @Builder.Default
    @Column(name = "fecha_devolucion")
    private LocalDateTime fechaDevolucion = LocalDateTime.now();

    @Column(name = "usuario_devolucion", length = 100)
    private String usuarioDevolucion;
}
