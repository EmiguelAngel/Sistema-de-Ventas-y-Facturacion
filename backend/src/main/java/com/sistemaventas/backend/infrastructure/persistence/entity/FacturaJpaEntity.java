package com.sistemaventas.backend.infrastructure.persistence.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/** Entidad JPA para la tabla FACTURA. Usa @GeneratedValue — elimina la generación manual de IDs. */
@Entity
@Table(name = "FACTURA")
public class FacturaJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDFACTURA")
    private Integer idFactura;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IDUSUARIO", referencedColumnName = "IDUSUARIO")
    @NotNull
    @JsonIgnore
    private UsuarioJpaEntity usuario;

    @Column(name = "IDPAGO")
    private Integer idPago;

    @Column(name = "FECHA")
    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    private Date fecha;

    @Column(name = "SUBTOTAL", precision = 10, scale = 2)
    @DecimalMin("0.0")
    private BigDecimal subtotal;

    @Column(name = "IVA", precision = 10, scale = 2)
    @DecimalMin("0.0")
    private BigDecimal iva;

    @Column(name = "TOTAL", precision = 10, scale = 2)
    @DecimalMin("0.0")
    private BigDecimal total;

    @Column(name = "payment_id", length = 100)
    private String paymentId;

    @Column(name = "devuelta")
    private Boolean devuelta = false;

    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DetalleFacturaJpaEntity> detallesFactura = new ArrayList<>();

    @OneToOne(mappedBy = "factura", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private PagoJpaEntity pago;

    public FacturaJpaEntity() { this.fecha = new Date(); }

    public Integer getIdFactura() { return idFactura; }
    public void setIdFactura(Integer idFactura) { this.idFactura = idFactura; }
    public UsuarioJpaEntity getUsuario() { return usuario; }
    public void setUsuario(UsuarioJpaEntity usuario) { this.usuario = usuario; }
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
    public List<DetalleFacturaJpaEntity> getDetallesFactura() { return detallesFactura; }
    public void setDetallesFactura(List<DetalleFacturaJpaEntity> d) { this.detallesFactura = d; }
    public PagoJpaEntity getPago() { return pago; }
    public void setPago(PagoJpaEntity pago) { this.pago = pago; }
}
