package com.sistemaventas.backend.domain.model;

import java.math.BigDecimal;

/**
 * Modelo de dominio para Pago.
 * Nombre PagoDomain para evitar colisión con la entidad JPA durante la migración.
 */
public class PagoDomain {

    private Integer id;
    private Integer idFactura;
    private String metodoPago;
    private BigDecimal monto;
    private String numeroTarjeta;   // Últimos 4 dígitos enmascarados (****1234)
    private String nombreTitular;

    public PagoDomain() {}

    public PagoDomain(Integer id, Integer idFactura, String metodoPago, BigDecimal monto) {
        this.id = id;
        this.idFactura = idFactura;
        this.metodoPago = metodoPago;
        this.monto = monto;
    }

    // Getters / Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getIdFactura() { return idFactura; }
    public void setIdFactura(Integer idFactura) { this.idFactura = idFactura; }

    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }

    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }

    public String getNumeroTarjeta() { return numeroTarjeta; }
    public void setNumeroTarjeta(String numeroTarjeta) { this.numeroTarjeta = numeroTarjeta; }

    public String getNombreTitular() { return nombreTitular; }
    public void setNombreTitular(String nombreTitular) { this.nombreTitular = nombreTitular; }
}
