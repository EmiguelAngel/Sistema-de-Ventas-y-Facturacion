package com.sistemaventas.backend.infrastructure.persistence.repository;

import com.sistemaventas.backend.infrastructure.persistence.entity.DetalleFacturaJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetalleFacturaJpaRepository extends JpaRepository<DetalleFacturaJpaEntity, Integer> {
    List<DetalleFacturaJpaEntity> findByFactura_IdFactura(Integer idFactura);
}
