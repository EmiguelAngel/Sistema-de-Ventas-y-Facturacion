package com.sistemaventas.backend.domain.ports.in;

import com.sistemaventas.backend.dto.request.VentaRequest;
import com.sistemaventas.backend.dto.response.VentaResponse;

import java.util.List;
import java.util.Optional;

/** Puerto de entrada — casos de uso del flujo de Ventas. */
public interface ProcesarVentaUseCase {
    VentaResponse procesarVenta(VentaRequest request);
    void validarVenta(VentaRequest request);
    List<com.sistemaventas.backend.domain.model.Venta> obtenerTodasLasVentas();
    Optional<com.sistemaventas.backend.domain.model.Venta> buscarVentaPorId(Integer id);
    List<com.sistemaventas.backend.domain.model.Venta> buscarVentasPorUsuario(Integer idUsuario);
    List<com.sistemaventas.backend.domain.model.Venta> obtenerVentasDeHoy();
    byte[] generarFacturaPdf(Long id);
}
