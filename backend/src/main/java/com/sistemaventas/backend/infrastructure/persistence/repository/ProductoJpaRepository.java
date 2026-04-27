package com.sistemaventas.backend.infrastructure.persistence.repository;

import com.sistemaventas.backend.infrastructure.persistence.entity.ProductoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductoJpaRepository extends JpaRepository<ProductoJpaEntity, Integer> {

    List<ProductoJpaEntity> findByCategoria(String categoria);

    List<ProductoJpaEntity> findByDescripcionContainingIgnoreCaseOrCategoriaContainingIgnoreCase(
            String descripcion, String categoria);

    @Query("SELECT p FROM ProductoJpaEntity p WHERE p.cantidadDisponible <= :limite")
    List<ProductoJpaEntity> findConStockBajo(@Param("limite") Integer limite);

    @Query("SELECT DISTINCT p.categoria FROM ProductoJpaEntity p ORDER BY p.categoria")
    List<String> findDistinctCategorias();

    @Query("SELECT COALESCE(MAX(p.idProducto), 0) FROM ProductoJpaEntity p")
    Integer findMaxId();

    @Query("SELECT SUM(p.precioUnitario * p.cantidadDisponible) FROM ProductoJpaEntity p")
    BigDecimal calcularValorTotalInventario();
}
