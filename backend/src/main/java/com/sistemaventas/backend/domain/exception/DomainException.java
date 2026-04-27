package com.sistemaventas.backend.domain.exception;

/**
 * Excepción base para todas las excepciones de dominio.
 * Extiende RuntimeException para compatibilidad con @Transactional rollback.
 */
public class DomainException extends RuntimeException {
    public DomainException(String message) { super(message); }
    public DomainException(String message, Throwable cause) { super(message, cause); }
}
