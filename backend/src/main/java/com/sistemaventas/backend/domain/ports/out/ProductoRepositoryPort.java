package com.sistemaventas.backend.domain.ports.out;

import com.sistemaventas.backend.domain.model.Producto;

import java.util.List;
import java.util.Optional;

/** Puerto de salida — contrato que la capa de dominio exige al repositorio de Productos. */
public interface ProductoRepositoryPort {
    Optional<Producto> buscarPorId(Integer id);
    Producto guardar(Producto producto);
    List<Producto> buscarTodos();
    void eliminar(Integer id);
    boolean existePorId(Integer id);
    List<Producto> buscarPorCategoria(String categoria);
    List<Producto> buscarPorTermino(String termino);
    List<Producto> buscarConStockBajo(Integer limite);
    List<String> buscarCategorias();
}
