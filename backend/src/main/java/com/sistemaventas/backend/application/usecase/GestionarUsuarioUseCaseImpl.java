package com.sistemaventas.backend.application.usecase;

import com.sistemaventas.backend.domain.exception.UsuarioNoEncontradoException;
import com.sistemaventas.backend.domain.model.UsuarioDomain;
import com.sistemaventas.backend.domain.ports.in.GestionarUsuarioUseCase;
import com.sistemaventas.backend.domain.ports.out.UsuarioRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/** Caso de Uso — Gestión de Usuarios. */
@Service
public class GestionarUsuarioUseCaseImpl implements GestionarUsuarioUseCase {

    private static final Logger log = LoggerFactory.getLogger(GestionarUsuarioUseCaseImpl.class);

    private final UsuarioRepositoryPort repositoryPort;

    public GestionarUsuarioUseCaseImpl(UsuarioRepositoryPort repositoryPort) {
        this.repositoryPort = repositoryPort;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioDomain> obtenerTodos() {
        return repositoryPort.buscarTodos();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UsuarioDomain> buscarPorId(Integer id) {
        return repositoryPort.buscarPorId(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UsuarioDomain> verificarCredenciales(String correo, String contrasena) {
        return repositoryPort.buscarPorCorreo(correo)
                .filter(u -> u.getContrasena().equals(contrasena));
    }

    @Override
    @Transactional
    public UsuarioDomain crear(UsuarioDomain usuario) {
        if (repositoryPort.existePorCorreo(usuario.getCorreo())) {
            throw new IllegalArgumentException("Ya existe un usuario con ese correo: " + usuario.getCorreo());
        }
        UsuarioDomain creado = repositoryPort.guardar(usuario);
        log.info("Usuario creado con ID: {}", creado.getId());
        return creado;
    }

    @Override
    @Transactional
    public UsuarioDomain actualizar(Integer id, UsuarioDomain usuarioActualizado) {
        UsuarioDomain existente = repositoryPort.buscarPorId(id)
                .orElseThrow(() -> new UsuarioNoEncontradoException(id));

        if (!existente.getCorreo().equals(usuarioActualizado.getCorreo()) &&
                repositoryPort.existePorCorreo(usuarioActualizado.getCorreo())) {
            throw new IllegalArgumentException("Ya existe un usuario con ese correo");
        }

        existente.setNombre(usuarioActualizado.getNombre());
        existente.setCorreo(usuarioActualizado.getCorreo());
        existente.setTelefono(usuarioActualizado.getTelefono());
        existente.setIdRol(usuarioActualizado.getIdRol());
        if (usuarioActualizado.getContrasena() != null && !usuarioActualizado.getContrasena().isBlank()) {
            existente.setContrasena(usuarioActualizado.getContrasena());
        }
        return repositoryPort.guardar(existente);
    }

    @Override
    @Transactional
    public boolean eliminar(Integer id) {
        if (!repositoryPort.existePorId(id)) return false;
        repositoryPort.eliminar(id);
        log.info("Usuario ID {} eliminado", id);
        return true;
    }
}
