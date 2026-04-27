package com.sistemaventas.backend.domain.model;

/**
 * Modelo de dominio para Usuario.
 * Sin dependencias JPA ni Spring.
 */
public class UsuarioDomain {

    private Integer id;
    private String nombre;
    private String correo;
    private String contrasena;
    private String telefono;
    private Integer idRol;
    private String nombreRol;

    public UsuarioDomain() {}

    public UsuarioDomain(Integer id, String nombre, String correo,
                         String contrasena, String telefono,
                         Integer idRol, String nombreRol) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.contrasena = contrasena;
        this.telefono = telefono;
        this.idRol = idRol;
        this.nombreRol = nombreRol;
    }

    public boolean esAdministrador() {
        return "Administrador".equalsIgnoreCase(nombreRol);
    }

    // Getters / Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

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

    public String getNombreRol() { return nombreRol; }
    public void setNombreRol(String nombreRol) { this.nombreRol = nombreRol; }
}
