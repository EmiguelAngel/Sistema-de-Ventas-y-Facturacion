package com.sistemaventas.backend.domain.ports.in;

import com.sistemaventas.backend.domain.model.UsuarioDomain;

import java.util.List;
import java.util.Optional;

/** Puerto de entrada — casos de uso de Usuarios. */
public interface GestionarUsuarioUseCase {
    List<UsuarioDomain> obtenerTodos();
    Optional<UsuarioDomain> buscarPorId(Integer id);
    Optional<UsuarioDomain> verificarCredenciales(String correo, String contrasena);
    UsuarioDomain crear(UsuarioDomain usuario);
    UsuarioDomain actualizar(Integer id, UsuarioDomain usuario);
    boolean eliminar(Integer id);
}
