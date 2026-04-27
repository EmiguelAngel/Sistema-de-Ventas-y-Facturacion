package com.sistemaventas.backend.domain.model;

import java.math.BigDecimal;

/**
 * Value Object de dominio — representa un ítem dentro de una Venta.
 * Inmutable por diseño: el subtotal se calcula en construcción.
 */
public class ItemVenta {

    private Integer idProducto;
    private String descripcionProducto;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;

    public ItemVenta() {}

    public ItemVenta(Integer idProducto, String descripcionProducto,
                     Integer cantidad, BigDecimal precioUnitario) {
        this.idProducto = idProducto;
        this.descripcionProducto = descripcionProducto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = precioUnitario.multiply(new BigDecimal(cantidad));
    }

    // Getter/Setter
    public Integer getIdProducto() { return idProducto; }
    public void setIdProducto(Integer idProducto) { this.idProducto = idProducto; }

    public String getDescripcionProducto() { return descripcionProducto; }
    public void setDescripcionProducto(String descripcionProducto) { this.descripcionProducto = descripcionProducto; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
}
