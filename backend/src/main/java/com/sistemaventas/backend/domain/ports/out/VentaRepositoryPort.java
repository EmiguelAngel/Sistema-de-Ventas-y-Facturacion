package com.sistemaventas.backend.domain.ports.out;

import com.sistemaventas.backend.domain.model.Venta;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/** Puerto de salida — contrato que la capa de dominio exige al repositorio de Ventas (Facturas). */
public interface VentaRepositoryPort {
    Venta guardar(Venta venta);
    Optional<Venta> buscarPorId(Integer id);
    List<Venta> buscarTodas();
    List<Venta> buscarPorUsuario(Integer idUsuario);
    List<Venta> buscarDeHoy();
    List<Venta> buscarEntreFechas(Date inicio, Date fin);
    BigDecimal sumarTotalDelDia(Date fecha);
}
