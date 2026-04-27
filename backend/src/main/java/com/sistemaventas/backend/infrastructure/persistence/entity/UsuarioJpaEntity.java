package com.sistemaventas.backend.infrastructure.persistence.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.util.ArrayList;
import java.util.List;

/** Entidad JPA para la tabla USUARIO. */
@Entity
@Table(name = "USUARIO")
public class UsuarioJpaEntity {

    @Id
    @Column(name = "IDUSUARIO")
    private Integer idUsuario;

    @NotBlank @Size(max = 256)
    @Column(name = "NOMBRE", length = 256)
    private String nombre;

    @Email @NotBlank @Size(max = 256)
    @Column(name = "CORREO", length = 256, unique = true)
    private String correo;

    @NotBlank @Size(min = 6, max = 256)
    @Column(name = "CONTRASENA", length = 256)
    private String contrasena;

    @Size(max = 20)
    @Column(name = "TELEFONO", length = 20)
    private String telefono;

    @Column(name = "IDROL")
    private Integer idRol;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "IDROL", referencedColumnName = "IDROL", insertable = false, updatable = false)
    private RolJpaEntity rol;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<FacturaJpaEntity> facturas = new ArrayList<>();

    public UsuarioJpaEntity() {}

    public Integer getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public Integer getIdRol() { return idRol; }
    public void setIdRol(Integer idRol) { this.idRol = idRol; }
    public RolJpaEntity getRol() { return rol; }
    public void setRol(RolJpaEntity rol) { this.rol = rol; if (rol != null) this.idRol = rol.getIdRol(); }
    public List<FacturaJpaEntity> getFacturas() { return facturas; }
    public void setFacturas(List<FacturaJpaEntity> facturas) { this.facturas = facturas; }
}
