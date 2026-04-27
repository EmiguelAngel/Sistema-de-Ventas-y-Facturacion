package com.sistemaventas.backend.domain.ports.out;

import com.sistemaventas.backend.domain.model.UsuarioDomain;

import java.util.List;
import java.util.Optional;

/** Puerto de salida — contrato que la capa de dominio exige al repositorio de Usuarios. */
public interface UsuarioRepositoryPort {
    Optional<UsuarioDomain> buscarPorId(Integer id);
    Optional<UsuarioDomain> buscarPorCorreo(String correo);
    List<UsuarioDomain> buscarTodos();
    UsuarioDomain guardar(UsuarioDomain usuario);
    void eliminar(Integer id);
    boolean existePorId(Integer id);
    boolean existePorCorreo(String correo);
}
