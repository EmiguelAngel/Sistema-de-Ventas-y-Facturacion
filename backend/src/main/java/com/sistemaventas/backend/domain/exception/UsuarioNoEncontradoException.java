package com.sistemaventas.backend.domain.exception;

public class UsuarioNoEncontradoException extends DomainException {
    public UsuarioNoEncontradoException(Integer id) {
        super("Usuario no encontrado con ID: " + id);
    }
    public UsuarioNoEncontradoException(String correo) {
        super("Usuario no encontrado con correo: " + correo);
    }
}
