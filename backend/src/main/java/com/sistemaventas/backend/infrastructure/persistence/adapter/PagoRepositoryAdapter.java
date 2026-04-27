package com.sistemaventas.backend.infrastructure.persistence.adapter;

import com.sistemaventas.backend.domain.model.PagoDomain;
import com.sistemaventas.backend.domain.ports.out.PagoRepositoryPort;
import com.sistemaventas.backend.infrastructure.persistence.mapper.PagoMapper;
import com.sistemaventas.backend.infrastructure.persistence.repository.PagoJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

/** Adaptador de salida para Pagos. */
@Component
public class PagoRepositoryAdapter implements PagoRepositoryPort {

    private final PagoJpaRepository jpaRepository;
    private final PagoMapper mapper;

    public PagoRepositoryAdapter(PagoJpaRepository jpaRepository, PagoMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public PagoDomain guardar(PagoDomain pago) {
        return mapper.toDomain(jpaRepository.save(mapper.toJpaEntity(pago)));
    }

    @Override
    public Optional<PagoDomain> buscarPorId(Integer id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }
}
