package com.sistemaventas.backend.infrastructure.persistence.repository;

import com.sistemaventas.backend.infrastructure.persistence.entity.UsuarioJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioJpaRepository extends JpaRepository<UsuarioJpaEntity, Integer> {
    Optional<UsuarioJpaEntity> findByCorreo(String correo);
    boolean existsByCorreo(String correo);

    @Query("SELECT u FROM UsuarioJpaEntity u WHERE u.rol.idRol = :idRol")
    List<UsuarioJpaEntity> findByRolId(@Param("idRol") Integer idRol);

    @Query("SELECT u FROM UsuarioJpaEntity u WHERE LOWER(u.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    List<UsuarioJpaEntity> findByNombreContainingIgnoreCase(@Param("nombre") String nombre);
}
