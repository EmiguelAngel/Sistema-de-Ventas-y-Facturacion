package com.sistemaventas.backend.domain.ports.in;

import com.sistemaventas.backend.domain.model.Producto;

import java.util.List;
import java.util.Optional;

/** Puerto de entrada — casos de uso de consulta de Productos. */
public interface ConsultarProductoUseCase {
    List<Producto> obtenerTodos();
    Optional<Producto> buscarPorId(Integer id);
    List<Producto> buscarPorCategoria(String categoria);
    List<Producto> buscarPorTermino(String termino);
    List<Producto> buscarConStockBajo(Integer limite);
    List<String> obtenerCategorias();
}
