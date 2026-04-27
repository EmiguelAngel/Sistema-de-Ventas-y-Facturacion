package com.sistemaventas.backend.domain.exception;

public class ProductoNoEncontradoException extends DomainException {
    public ProductoNoEncontradoException(Integer id) {
        super("Producto no encontrado con ID: " + id);
    }
}
