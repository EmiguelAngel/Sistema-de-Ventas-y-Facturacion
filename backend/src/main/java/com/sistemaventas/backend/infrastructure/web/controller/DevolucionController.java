package com.sistemaventas.backend.infrastructure.web.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sistemaventas.backend.infrastructure.web.dto.DevolucionDTO;
import com.sistemaventas.backend.infrastructure.web.dto.DevolucionRequestDTO;
import com.sistemaventas.backend.domain.ports.in.ProcesarDevolucionUseCase;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/devoluciones")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@Slf4j
public class DevolucionController {

    private final ProcesarDevolucionUseCase devolucionUseCase;

    public DevolucionController(ProcesarDevolucionUseCase devolucionUseCase) {
        this.devolucionUseCase = devolucionUseCase;
    }

    @PostMapping("/procesar")
    public ResponseEntity<?> procesarDevolucion(@Valid @RequestBody DevolucionRequestDTO request) {
        try {
            log.info("Recibida solicitud de devolución para factura ID: {}", request.getIdFactura());
            DevolucionDTO devolucion = devolucionUseCase.procesarDevolucion(request);
            return ResponseEntity.ok(devolucion);
        } catch (IllegalStateException e) {
            log.warn("Error de validación al procesar devolución: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Error al procesar devolución: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al procesar la devolución: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<DevolucionDTO>> obtenerTodasLasDevoluciones() {
        try {
            return ResponseEntity.ok(devolucionUseCase.obtenerTodas());
        } catch (Exception e) {
            log.error("Error al obtener devoluciones: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/factura/{idFactura}")
    public ResponseEntity<List<DevolucionDTO>> obtenerDevolucionesPorFactura(@PathVariable Integer idFactura) {
        try {
            return ResponseEntity.ok(devolucionUseCase.buscarPorFactura(idFactura));
        } catch (Exception e) {
            log.error("Error al obtener devoluciones de factura {}: {}", idFactura, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<DevolucionDTO>> obtenerDevolucionesPorEstado(@PathVariable String estado) {
        try {
            return ResponseEntity.ok(devolucionUseCase.buscarPorEstado(estado));
        } catch (Exception e) {
            log.error("Error al obtener devoluciones por estado {}: {}", estado, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerDevolucionPorId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(devolucionUseCase.buscarPorId(id));
        } catch (Exception e) {
            log.error("Error al obtener devolución {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener la devolución: " + e.getMessage());
        }
    }
}
