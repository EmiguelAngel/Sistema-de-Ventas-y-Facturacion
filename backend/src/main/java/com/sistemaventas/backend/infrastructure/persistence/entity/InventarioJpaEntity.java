package com.sistemaventas.backend.infrastructure.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.Date;

/** Entidad JPA para la tabla INVENTARIO. */
@Entity
@Table(name = "INVENTARIO")
public class InventarioJpaEntity {

    @Id
    @Column(name = "IDINVENTARIO")
    private Integer idInventario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IDPRODUCTO", referencedColumnName = "IDPRODUCTO")
    @NotNull
    private ProductoJpaEntity producto;

    @Column(name = "FECHAACTUALIZACION")
    @Temporal(TemporalType.DATE)
    @NotNull
    private Date fechaActualizacion;

    @Column(name = "CANTIDADDISPONIBLE")
    @Min(0)
    private Integer cantidadDisponible;

    public InventarioJpaEntity() { this.fechaActualizacion = new Date(); }

    public Integer getIdInventario() { return idInventario; }
    public void setIdInventario(Integer idInventario) { this.idInventario = idInventario; }
    public ProductoJpaEntity getProducto() { return producto; }
    public void setProducto(ProductoJpaEntity producto) { this.producto = producto; }
    public Date getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(Date fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }
    public Integer getCantidadDisponible() { return cantidadDisponible; }
    public void setCantidadDisponible(Integer cantidadDisponible) { this.cantidadDisponible = cantidadDisponible; }
}
