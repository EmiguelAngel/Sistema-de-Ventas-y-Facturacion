package com.sistemaventas.backend.entity;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * POJO legacy — usado por UsuarioController como cuerpo de request/response HTTP.
 * NO es una entidad JPA; el mapeo ORM lo hace UsuarioJpaEntity.
 */
public class Usuario {

    private Integer idUsuario;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 256, message = "El nombre no puede exceder 256 caracteres")
    private String nombre;

    @Email(message = "El formato del correo no es válido")
    @NotBlank(message = "El correo es obligatorio")
    @Size(max = 256, message = "El correo no puede exceder 256 caracteres")
    private String correo;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, max = 256, message = "La contraseña debe tener entre 6 y 256 caracteres")
    private String contrasena;

    @Size(max = 20, message = "El teléfono no puede exceder 20 caracteres")
    private String telefono;

    private Integer idRol;

    @JsonIgnore
    private List<Factura> facturas = new ArrayList<>();

    public Usuario() {}

    public Usuario(Integer idUsuario, String nombre, String correo, String contrasena, String telefono) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.correo = correo;
        this.contrasena = contrasena;
        this.telefono = telefono;
    }

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

    public List<Factura> getFacturas() { return facturas; }
    public void setFacturas(List<Factura> facturas) { this.facturas = facturas; }

    @Override
    public String toString() {
        return "Usuario{idUsuario=" + idUsuario + ", nombre='" + nombre + "', correo='" + correo + "'}";
    }
}
