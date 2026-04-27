package com.sistemaventas.backend.entity;

import java.math.BigDecimal;

/**
 * POJO legacy — usado por VentaResponse y ProcesarVentaUseCaseImpl.
 * NO es una entidad JPA; el mapeo ORM lo hace PagoJpaEntity.
 */
public class Pago {

    private Integer idPago;
    private Factura factura;
    private String metodoPago;
    private BigDecimal monto;
    private String numeroTarjeta;
    private String nombreTitular;

    public Pago() {}

    public Pago(Integer idPago, Factura factura, String metodoPago, BigDecimal monto) {
        this.idPago = idPago;
        this.factura = factura;
        this.metodoPago = metodoPago;
        this.monto = monto;
    }

    public Integer getIdPago() { return idPago; }
    public void setIdPago(Integer idPago) { this.idPago = idPago; }

    public Factura getFactura() { return factura; }
    public void setFactura(Factura factura) { this.factura = factura; }

    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }

    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }

    public String getNumeroTarjeta() { return numeroTarjeta; }
    public void setNumeroTarjeta(String numeroTarjeta) { this.numeroTarjeta = numeroTarjeta; }

    public String getNombreTitular() { return nombreTitular; }
    public void setNombreTitular(String nombreTitular) { this.nombreTitular = nombreTitular; }

    @Override
    public String toString() {
        return "Pago{idPago=" + idPago + ", metodoPago='" + metodoPago + "', monto=" + monto + '}';
    }
}
