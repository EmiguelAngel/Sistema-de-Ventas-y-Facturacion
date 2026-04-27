package com.sistemaventas.backend.domain.exception;

public class StockInsuficienteException extends DomainException {
    public StockInsuficienteException(String producto, int disponible, int solicitado) {
        super(String.format("Stock insuficiente para '%s'. Disponible: %d, Solicitado: %d",
                producto, disponible, solicitado));
    }
}
