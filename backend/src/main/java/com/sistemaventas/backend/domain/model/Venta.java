package com.sistemaventas.backend.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Agregado raíz de dominio — representa una Venta completa (factura).
 * Contiene la lógica de cálculo de totales con IVA.
 * No tiene dependencias de frameworks.
 */
public class Venta {

    private static final BigDecimal TASA_IVA = new BigDecimal("0.19");

    private Integer id;
    private Integer idUsuario;
    private String nombreUsuario;
    private Integer idPago;
    private Date fecha;
    private BigDecimal subtotal;
    private BigDecimal iva;
    private BigDecimal total;
    private String paymentId;
    private Boolean devuelta = false;
    private List<ItemVenta> items = new ArrayList<>();

    public Venta() {
        this.fecha = new Date();
    }

    // ── Lógica de negocio ─────────────────────────────────────────────────────

    /**
     * Recalcula subtotal, IVA (19 %) y total a partir de los ítems.
     * Debe invocarse cada vez que se modifique la lista de ítems.
     */
    public void calcularTotales() {
        this.subtotal = items.stream()
                .map(ItemVenta::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        this.iva = this.subtotal.multiply(TASA_IVA).setScale(2, RoundingMode.HALF_UP);
        this.total = this.subtotal.add(this.iva).setScale(2, RoundingMode.HALF_UP);
    }

    public void agregarItem(ItemVenta item) {
        this.items.add(item);
        calcularTotales();
    }

    public boolean estaDevuelta() {
        return Boolean.TRUE.equals(devuelta);
    }

    // ── Getters / Setters ─────────────────────────────────────────────────────

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; }

    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }

    public Integer getIdPago() { return idPago; }
    public void setIdPago(Integer idPago) { this.idPago = idPago; }

    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public BigDecimal getIva() { return iva; }
    public void setIva(BigDecimal iva) { this.iva = iva; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }

    public Boolean getDevuelta() { return devuelta; }
    public void setDevuelta(Boolean devuelta) { this.devuelta = devuelta; }

    public List<ItemVenta> getItems() { return items; }
    public void setItems(List<ItemVenta> items) {
        this.items = items;
        calcularTotales();
    }

    @Override
    public String toString() {
        return "Venta{id=" + id + ", total=" + total + ", items=" + items.size() + "}";
    }
}
