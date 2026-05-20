package com.sistemaventas.backend.domain.ports.in;

import com.sistemaventas.backend.infrastructure.web.dto.DevolucionDTO;
import com.sistemaventas.backend.infrastructure.web.dto.DevolucionRequestDTO;

import java.util.List;

/** Puerto de entrada — casos de uso de Devoluciones. */
public interface ProcesarDevolucionUseCase {
    DevolucionDTO procesarDevolucion(DevolucionRequestDTO request);
    List<DevolucionDTO> obtenerTodas();
    DevolucionDTO buscarPorId(Long id);
    List<DevolucionDTO> buscarPorFactura(Integer idFactura);
    List<DevolucionDTO> buscarPorEstado(String estado);
}
