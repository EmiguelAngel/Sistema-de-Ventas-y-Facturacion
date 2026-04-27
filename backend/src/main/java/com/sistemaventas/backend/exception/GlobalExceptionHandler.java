package com.sistemaventas.backend.exception;

import com.sistemaventas.backend.domain.exception.DomainException;
import com.sistemaventas.backend.domain.exception.StockInsuficienteException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(e -> errors.put(e.getField(), e.getDefaultMessage()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraint(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(v -> errors.put(v.getPropertyPath().toString(), v.getMessage()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    /** Stock insuficiente → 409 Conflict */
    @ExceptionHandler(StockInsuficienteException.class)
    public ResponseEntity<Map<String, String>> handleStockInsuficiente(StockInsuficienteException ex) {
        return errorResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    /** Cualquier excepción de dominio genérica → 422 Unprocessable Entity */
    @ExceptionHandler(DomainException.class)
    public ResponseEntity<Map<String, String>> handleDomain(DomainException ex) {
        return errorResponse(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException ex) {
        return errorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleIllegalState(IllegalStateException ex) {
        return errorResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(ResourceNotFoundException ex) {
        return errorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntime(RuntimeException ex) {
        return errorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    private ResponseEntity<Map<String, String>> errorResponse(HttpStatus status, String message) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        return ResponseEntity.status(status).body(error);
    }
}