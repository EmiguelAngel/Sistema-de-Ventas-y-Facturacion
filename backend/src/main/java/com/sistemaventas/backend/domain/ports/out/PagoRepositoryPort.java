package com.sistemaventas.backend.domain.ports.out;

import com.sistemaventas.backend.domain.model.PagoDomain;

import java.util.Optional;

/** Puerto de salida — contrato que la capa de dominio exige al repositorio de Pagos. */
public interface PagoRepositoryPort {
    PagoDomain guardar(PagoDomain pago);
    Optional<PagoDomain> buscarPorId(Integer id);
}
