package com.sistemaventas.backend.infrastructure.persistence.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad JPA para la tabla PRODUCTO.
 * Separada del modelo de dominio — solo contiene mapeo ORM, sin lógica de negocio.
 */
@Entity
@Table(name = "PRODUCTO")
public class ProductoJpaEntity {

    @Id
    @Column(name = "IDPRODUCTO")
    private Integer idProducto;

    @Column(name = "CANTIDADDISPONIBLE")
    @Min(value = 0, message = "La cantidad disponible no puede ser negativa")
    private Integer cantidadDisponible;

    @Column(name = "PRECIOUNITARIO", precision = 10, scale = 2)
    @DecimalMin(value = "0.0", message = "El precio unitario debe ser mayor a 0")
    private BigDecimal precioUnitario;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 1024)
    @Column(name = "DESCRIPCION", length = 1024)
    private String descripcion;

    @NotBlank(message = "La categoría es obligatoria")
    @Size(max = 100)
    @Column(name = "CATEGORIA", length = 100)
    private String categoria;

    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<DetalleFacturaJpaEntity> detallesFactura = new ArrayList<>();

    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<InventarioJpaEntity> inventarios = new ArrayList<>();

    public ProductoJpaEntity() {}

    public Integer getIdProducto() { return idProducto; }
    public void setIdProducto(Integer idProducto) { this.idProducto = idProducto; }
    public Integer getCantidadDisponible() { return cantidadDisponible; }
    public void setCantidadDisponible(Integer cantidadDisponible) { this.cantidadDisponible = cantidadDisponible; }
    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public List<DetalleFacturaJpaEntity> getDetallesFactura() { return detallesFactura; }
    public void setDetallesFactura(List<DetalleFacturaJpaEntity> detallesFactura) { this.detallesFactura = detallesFactura; }
    public List<InventarioJpaEntity> getInventarios() { return inventarios; }
    public void setInventarios(List<InventarioJpaEntity> inventarios) { this.inventarios = inventarios; }
}
