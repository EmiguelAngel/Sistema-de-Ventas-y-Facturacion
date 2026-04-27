package com.sistemaventas.backend.domain.ports.out;

import com.sistemaventas.backend.dto.DevolucionDTO;

import java.util.List;
import java.util.Optional;

/** Puerto de salida — contrato para el repositorio de Devoluciones. */
public interface DevolucionRepositoryPort {
    DevolucionDTO guardar(DevolucionDTO devolucion);
    Optional<DevolucionDTO> buscarPorId(Long id);
    List<DevolucionDTO> buscarPorFactura(Integer idFactura);
    List<DevolucionDTO> buscarPorEstado(String estado);
    List<DevolucionDTO> buscarTodas();
}
