package com.sistemaventas.backend.infrastructure.persistence.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/** Entidad JPA para la tabla PAGO. Usa @GeneratedValue — elimina generación manual. */
@Entity
@Table(name = "PAGO")
public class PagoJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDPAGO")
    private Integer idPago;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IDFACTURA", referencedColumnName = "IDFACTURA")
    @NotNull
    @JsonIgnore
    private FacturaJpaEntity factura;

    @NotBlank @Size(max = 256)
    @Column(name = "METODOPAGO", length = 256)
    private String metodoPago;

    @Column(name = "MONTO", precision = 10, scale = 2)
    @DecimalMin("0.0") @NotNull
    private BigDecimal monto;

    @Column(name = "NUMERO_TARJETA", length = 20)
    private String numeroTarjeta;

    @Column(name = "NOMBRE_TITULAR", length = 100)
    private String nombreTitular;

    public PagoJpaEntity() {}

    public Integer getIdPago() { return idPago; }
    public void setIdPago(Integer idPago) { this.idPago = idPago; }
    public FacturaJpaEntity getFactura() { return factura; }
    public void setFactura(FacturaJpaEntity factura) { this.factura = factura; }
    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }
    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }
    public String getNumeroTarjeta() { return numeroTarjeta; }
    public void setNumeroTarjeta(String numeroTarjeta) { this.numeroTarjeta = numeroTarjeta; }
    public String getNombreTitular() { return nombreTitular; }
    public void setNombreTitular(String nombreTitular) { this.nombreTitular = nombreTitular; }
}
