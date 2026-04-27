package com.sistemaventas.backend.infrastructure.persistence.repository;

import com.sistemaventas.backend.infrastructure.persistence.entity.DevolucionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DevolucionJpaRepository extends JpaRepository<DevolucionJpaEntity, Long> {
    List<DevolucionJpaEntity> findByFactura_IdFactura(Integer idFactura);
    Optional<DevolucionJpaEntity> findByPaymentId(String paymentId);
    List<DevolucionJpaEntity> findByEstado(String estado);
    Optional<DevolucionJpaEntity> findByRefundId(String refundId);
}
