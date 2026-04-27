package com.sistemaventas.backend.infrastructure.persistence.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

/** Entidad JPA para la tabla ROL. */
@Entity
@Table(name = "ROL")
public class RolJpaEntity {

    @Id
    @Column(name = "IDROL")
    private Integer idRol;

    @NotBlank
    @Size(max = 256)
    @Column(name = "NOMBREROL", length = 256)
    private String nombreRol;

    @OneToMany(mappedBy = "rol", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<UsuarioJpaEntity> usuarios = new ArrayList<>();

    public RolJpaEntity() {}

    public Integer getIdRol() { return idRol; }
    public void setIdRol(Integer idRol) { this.idRol = idRol; }
    public String getNombreRol() { return nombreRol; }
    public void setNombreRol(String nombreRol) { this.nombreRol = nombreRol; }
    public List<UsuarioJpaEntity> getUsuarios() { return usuarios; }
    public void setUsuarios(List<UsuarioJpaEntity> usuarios) { this.usuarios = usuarios; }
}
