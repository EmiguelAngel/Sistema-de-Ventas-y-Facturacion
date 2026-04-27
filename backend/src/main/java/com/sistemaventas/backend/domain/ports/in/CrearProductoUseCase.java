package com.sistemaventas.backend.domain.ports.in;

import com.sistemaventas.backend.domain.model.Producto;
import com.sistemaventas.backend.dto.request.ProductoRequest;

/** Puerto de entrada — casos de uso de creación y modificación de Productos. */
public interface CrearProductoUseCase {
    Producto crearProducto(ProductoRequest request);
    Producto actualizarProducto(Integer id, ProductoRequest request);
    Producto actualizarStock(Integer id, Integer nuevaCantidad);
    Producto reducirStock(Integer id, Integer cantidad);
    Producto aumentarStock(Integer id, Integer cantidad);
    boolean eliminarProducto(Integer id);
}
