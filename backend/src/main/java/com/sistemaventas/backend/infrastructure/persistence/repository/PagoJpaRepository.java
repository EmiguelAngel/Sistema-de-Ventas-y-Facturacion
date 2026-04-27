package com.sistemaventas.backend.infrastructure.persistence.repository;

import com.sistemaventas.backend.infrastructure.persistence.entity.PagoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PagoJpaRepository extends JpaRepository<PagoJpaEntity, Integer> {

    List<PagoJpaEntity> findByMetodoPago(String metodoPago);

    @Query("SELECT DISTINCT p.metodoPago FROM PagoJpaEntity p ORDER BY p.metodoPago")
    List<String> findDistinctMetodosPago();

    @Query("SELECT p FROM PagoJpaEntity p WHERE p.factura.idFactura = :idFactura")
    List<PagoJpaEntity> findByFacturaId(@Param("idFactura") Integer idFactura);
}
