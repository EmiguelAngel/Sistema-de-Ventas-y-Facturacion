package com.sistemaventas.backend.domain.exception;

public class FacturaNoEncontradaException extends DomainException {
    public FacturaNoEncontradaException(Integer id) {
        super("Factura no encontrada con ID: " + id);
    }
}
