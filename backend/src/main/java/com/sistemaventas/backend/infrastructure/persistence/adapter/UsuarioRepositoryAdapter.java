package com.sistemaventas.backend.infrastructure.persistence.adapter;

import com.sistemaventas.backend.domain.model.UsuarioDomain;
import com.sistemaventas.backend.domain.ports.out.UsuarioRepositoryPort;
import com.sistemaventas.backend.infrastructure.persistence.mapper.UsuarioMapper;
import com.sistemaventas.backend.infrastructure.persistence.repository.UsuarioJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/** Adaptador de salida para Usuarios. */
@Component
public class UsuarioRepositoryAdapter implements UsuarioRepositoryPort {

    private final UsuarioJpaRepository jpaRepository;
    private final UsuarioMapper mapper;

    public UsuarioRepositoryAdapter(UsuarioJpaRepository jpaRepository, UsuarioMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<UsuarioDomain> buscarPorId(Integer id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<UsuarioDomain> buscarPorCorreo(String correo) {
        return jpaRepository.findByCorreo(correo).map(mapper::toDomain);
    }

    @Override
    public List<UsuarioDomain> buscarTodos() {
        return mapper.toDomainList(jpaRepository.findAll());
    }

    @Override
    public UsuarioDomain guardar(UsuarioDomain usuario) {
        return mapper.toDomain(jpaRepository.save(mapper.toJpaEntity(usuario)));
    }

    @Override
    public void eliminar(Integer id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existePorId(Integer id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public boolean existePorCorreo(String correo) {
        return jpaRepository.existsByCorreo(correo);
    }
}
