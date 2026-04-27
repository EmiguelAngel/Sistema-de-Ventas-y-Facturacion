package com.sistemaventas.backend.domain.model;

import java.math.BigDecimal;

/**
 * Entidad de Dominio — Java puro, sin dependencias de frameworks.
 * Contiene toda la lógica de negocio relativa a productos e inventario.
 */
public class Producto {

    private Integer id;
    private String descripcion;
    private BigDecimal precioUnitario;
    private Integer cantidadDisponible;
    private String categoria;

    public Producto() {}

    public Producto(Integer id, String descripcion, BigDecimal precioUnitario,
                    Integer cantidadDisponible, String categoria) {
        this.id = id;
        this.descripcion = descripcion;
        this.precioUnitario = precioUnitario;
        this.cantidadDisponible = cantidadDisponible;
        this.categoria = categoria;
    }

    // ── Lógica de negocio ─────────────────────────────────────────────────────

    public boolean tieneStock() {
        return cantidadDisponible != null && cantidadDisponible > 0;
    }

    public boolean tieneStockSuficiente(int cantidadRequerida) {
        return cantidadDisponible != null && cantidadDisponible >= cantidadRequerida;
    }

    /**
     * Reduce el stock del producto. Lanza {@link com.sistemaventas.backend.domain.exception.StockInsuficienteException}
     * si no hay suficiente inventario — la excepción se usa en los Use Cases.
     */
    public void reducirStock(int cantidad) {
        if (!tieneStockSuficiente(cantidad)) {
            throw new com.sistemaventas.backend.domain.exception.StockInsuficienteException(
                    descripcion, cantidadDisponible == null ? 0 : cantidadDisponible, cantidad);
        }
        this.cantidadDisponible -= cantidad;
    }

    public void aumentarStock(int cantidad) {
        if (cantidad <= 0) throw new IllegalArgumentException("La cantidad a aumentar debe ser mayor a 0");
        this.cantidadDisponible = (cantidadDisponible == null ? 0 : cantidadDisponible) + cantidad;
    }

    // ── Getters / Setters ─────────────────────────────────────────────────────

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }

    public Integer getCantidadDisponible() { return cantidadDisponible; }
    public void setCantidadDisponible(Integer cantidadDisponible) { this.cantidadDisponible = cantidadDisponible; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    @Override
    public String toString() {
        return "Producto{id=" + id + ", descripcion='" + descripcion + "', precio=" + precioUnitario
                + ", stock=" + cantidadDisponible + ", categoria='" + categoria + "'}";
    }
}
