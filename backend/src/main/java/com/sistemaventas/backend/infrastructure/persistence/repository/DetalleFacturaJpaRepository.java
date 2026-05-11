package com.sistemaventas.backend.infrastructure.persistence.repository;

import com.sistemaventas.backend.infrastructure.persistence.entity.DetalleFacturaJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetalleFacturaJpaRepository extends JpaRepository<DetalleFacturaJpaEntity, Integer> {
    List<DetalleFacturaJpaEntity> findByFactura_IdFactura(Integer idFactura);

    @Query("SELECT COALESCE(MAX(d.idDetalle), 0) FROM DetalleFacturaJpaEntity d")
    int findMaxIdDetalle();
}
