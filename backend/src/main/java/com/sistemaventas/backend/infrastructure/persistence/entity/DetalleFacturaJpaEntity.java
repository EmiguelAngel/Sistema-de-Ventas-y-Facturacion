package com.sistemaventas.backend.infrastructure.persistence.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/** Entidad JPA para la tabla DETALLEFACTURA. Usa @GeneratedValue — elimina generación manual. */
@Entity
@Table(name = "DETALLEFACTURA")
public class DetalleFacturaJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDDETALLE")
    private Integer idDetalle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IDPRODUCTO", referencedColumnName = "IDPRODUCTO")
    @NotNull
    @JsonIgnore
    private ProductoJpaEntity producto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IDFACTURA", referencedColumnName = "IDFACTURA")
    @NotNull
    @JsonIgnore
    private FacturaJpaEntity factura;

    @Column(name = "CANTIDAD")
    @Min(1) @NotNull
    private Integer cantidad;

    @Column(name = "PRECIOUNITARIO", precision = 10, scale = 2)
    @DecimalMin("0.0") @NotNull
    private BigDecimal precioUnitario;

    @Column(name = "SUBTOTAL", precision = 10, scale = 2)
    @DecimalMin("0.0")
    private BigDecimal subtotal;

    public DetalleFacturaJpaEntity() {}

    public Integer getIdDetalle() { return idDetalle; }
    public void setIdDetalle(Integer idDetalle) { this.idDetalle = idDetalle; }
    public ProductoJpaEntity getProducto() { return producto; }
    public void setProducto(ProductoJpaEntity producto) { this.producto = producto; }
    public FacturaJpaEntity getFactura() { return factura; }
    public void setFactura(FacturaJpaEntity factura) { this.factura = factura; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
}
