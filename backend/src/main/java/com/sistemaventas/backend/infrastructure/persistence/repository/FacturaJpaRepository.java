package com.sistemaventas.backend.infrastructure.persistence.repository;

import com.sistemaventas.backend.infrastructure.persistence.entity.FacturaJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Repository
public interface FacturaJpaRepository extends JpaRepository<FacturaJpaEntity, Integer> {

    @Query("SELECT f FROM FacturaJpaEntity f WHERE f.usuario.idUsuario = :idUsuario")
    List<FacturaJpaEntity> findByUsuarioId(@Param("idUsuario") Integer idUsuario);

    List<FacturaJpaEntity> findByFechaBetween(Date fechaInicio, Date fechaFin);

    @Query("SELECT f FROM FacturaJpaEntity f WHERE DATE(f.fecha) = DATE(:fecha)")
    List<FacturaJpaEntity> findFacturasDeHoy(@Param("fecha") Date fecha);

    @Query("SELECT SUM(f.total) FROM FacturaJpaEntity f WHERE DATE(f.fecha) = DATE(:fecha)")
    BigDecimal sumTotalVentasByFecha(@Param("fecha") Date fecha);

    List<FacturaJpaEntity> findAllByOrderByFechaDesc();
}
