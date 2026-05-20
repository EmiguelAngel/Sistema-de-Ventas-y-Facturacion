package com.sistemaventas.backend.infrastructure.web.dto.response;

import java.math.BigDecimal;

public class ProductoResponse {
    private Integer idProducto;
    private String descripcion;
    private BigDecimal precioUnitario;
    private Integer cantidadDisponible;
    private String categoria;

    public ProductoResponse() {}

    public ProductoResponse(Integer idProducto, String descripcion, BigDecimal precioUnitario,
                           Integer cantidadDisponible, String categoria) {
        this.idProducto = idProducto;
        this.descripcion = descripcion;
        this.precioUnitario = precioUnitario;
        this.cantidadDisponible = cantidadDisponible;
        this.categoria = categoria;
    }

    public Integer getIdProducto() { return idProducto; }
    public void setIdProducto(Integer idProducto) { this.idProducto = idProducto; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }

    public Integer getCantidadDisponible() { return cantidadDisponible; }
    public void setCantidadDisponible(Integer cantidadDisponible) { this.cantidadDisponible = cantidadDisponible; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
}
