package com.sistemaventas.backend.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * POJO legacy — usado por VentaResponse, FacturaPdfService y ProcesarVentaUseCaseImpl.
 * NO es una entidad JPA; el mapeo ORM lo hace FacturaJpaEntity.
 */
public class Factura {

    private Integer idFactura;
    private Usuario usuario;
    private Integer idPago;
    private Date fecha;
    private BigDecimal subtotal;
    private BigDecimal iva;
    private BigDecimal total;
    private String paymentId;
    private Boolean devuelta = false;
    private List<DetalleFactura> detallesFactura = new ArrayList<>();
    private Pago pago;

    public Factura() {
        this.fecha = new Date();
    }

    public Factura(Integer idFactura, Usuario usuario, Date fecha,
                   BigDecimal subtotal, BigDecimal iva, BigDecimal total) {
        this.idFactura = idFactura;
        this.usuario = usuario;
        this.fecha = fecha != null ? fecha : new Date();
        this.subtotal = subtotal;
        this.iva = iva;
        this.total = total;
    }

    public Integer getIdFactura() { return idFactura; }
    public void setIdFactura(Integer idFactura) { this.idFactura = idFactura; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

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

    public List<DetalleFactura> getDetallesFactura() { return detallesFactura; }
    public void setDetallesFactura(List<DetalleFactura> detallesFactura) { this.detallesFactura = detallesFactura; }

    public Pago getPago() { return pago; }
    public void setPago(Pago pago) { this.pago = pago; }

    public void calcularTotales() {
        this.subtotal = detallesFactura.stream()
                .map(DetalleFactura::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        this.iva = this.subtotal.multiply(new BigDecimal("0.19"));
        this.total = this.subtotal.add(this.iva);
    }

    public void agregarDetalle(DetalleFactura detalle) {
        this.detallesFactura.add(detalle);
        detalle.setFactura(this);
        calcularTotales();
    }

    @Override
    public String toString() {
        return "Factura{idFactura=" + idFactura + ", total=" + total + '}';
    }
}
