package com.sistemaventas.backend.entity;

import java.math.BigDecimal;

/**
 * POJO legacy — usado por VentaResponse, FacturaPdfService y ProcesarVentaUseCaseImpl.
 * NO es una entidad JPA; el mapeo ORM lo hace DetalleFacturaJpaEntity.
 */
public class DetalleFactura {

    private Integer idDetalle;
    private Producto producto;
    private Factura factura;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;

    public DetalleFactura() {}

    public DetalleFactura(Integer idDetalle, Producto producto, Factura factura,
                          Integer cantidad, BigDecimal precioUnitario) {
        this.idDetalle = idDetalle;
        this.producto = producto;
        this.factura = factura;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        if (cantidad != null && precioUnitario != null) {
            this.subtotal = precioUnitario.multiply(new BigDecimal(cantidad));
        }
    }

    public Integer getIdDetalle() { return idDetalle; }
    public void setIdDetalle(Integer idDetalle) { this.idDetalle = idDetalle; }

    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }

    public Factura getFactura() { return factura; }
    public void setFactura(Factura factura) { this.factura = factura; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
        if (this.precioUnitario != null && cantidad != null) {
            this.subtotal = this.precioUnitario.multiply(new BigDecimal(cantidad));
        }
    }

    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
        if (precioUnitario != null && this.cantidad != null) {
            this.subtotal = precioUnitario.multiply(new BigDecimal(this.cantidad));
        }
    }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public void calcularSubtotal() {
        this.subtotal = (this.precioUnitario != null && this.cantidad != null)
                ? this.precioUnitario.multiply(new BigDecimal(this.cantidad))
                : BigDecimal.ZERO;
    }

    @Override
    public String toString() {
        return "DetalleFactura{idDetalle=" + idDetalle + ", cantidad=" + cantidad + ", subtotal=" + subtotal + '}';
    }
}
