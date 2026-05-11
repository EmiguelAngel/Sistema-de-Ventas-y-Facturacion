package com.sistemaventas.backend.infrastructure.persistence.adapter;

import com.sistemaventas.backend.domain.model.PagoDomain;
import com.sistemaventas.backend.domain.ports.out.PagoRepositoryPort;
import com.sistemaventas.backend.infrastructure.persistence.entity.FacturaJpaEntity;
import com.sistemaventas.backend.infrastructure.persistence.entity.PagoJpaEntity;
import com.sistemaventas.backend.infrastructure.persistence.mapper.PagoMapper;
import com.sistemaventas.backend.infrastructure.persistence.repository.FacturaJpaRepository;
import com.sistemaventas.backend.infrastructure.persistence.repository.PagoJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

/** Adaptador de salida para Pagos. */
@Component
public class PagoRepositoryAdapter implements PagoRepositoryPort {

    private final PagoJpaRepository jpaRepository;
    private final FacturaJpaRepository facturaJpaRepository;
    private final PagoMapper mapper;

    public PagoRepositoryAdapter(PagoJpaRepository jpaRepository,
                                  FacturaJpaRepository facturaJpaRepository,
                                  PagoMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.facturaJpaRepository = facturaJpaRepository;
        this.mapper = mapper;
    }

    @Override
    public PagoDomain guardar(PagoDomain pago) {
        PagoJpaEntity entity = mapper.toJpaEntity(pago);
        if (pago.getIdFactura() != null) {
            FacturaJpaEntity factura = facturaJpaRepository.findById(pago.getIdFactura())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Factura no encontrada al guardar pago: " + pago.getIdFactura()));
            entity.setFactura(factura);
        }
        // Efectivo u otros métodos sin tarjeta: la BD suele tener NOT NULL en columnas de tarjeta.
        if (entity.getNumeroTarjeta() == null) {
            entity.setNumeroTarjeta("");
        }
        if (entity.getNombreTitular() == null) {
            entity.setNombreTitular("");
        }
        if (entity.getIdPago() == null) {
            entity.setIdPago(jpaRepository.findMaxIdPago() + 1);
        }
        return mapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<PagoDomain> buscarPorId(Integer id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }
}
