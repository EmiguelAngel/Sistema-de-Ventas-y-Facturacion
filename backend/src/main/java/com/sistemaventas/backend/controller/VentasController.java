package com.sistemaventas.backend.controller;

import com.sistemaventas.backend.domain.model.Venta;
import com.sistemaventas.backend.domain.ports.in.ProcesarVentaUseCase;
import com.sistemaventas.backend.dto.request.VentaRequest;
import com.sistemaventas.backend.dto.response.VentaResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST de Ventas — adaptador HTTP.
 * Inyecta solo el Use Case ProcesarVentaUseCase.
 * Eliminados: endpoints de debug (/test-simple, /debug, /demo-facade, /ping).
 */
@RestController
@RequestMapping("/api/ventas")
@CrossOrigin(origins = "http://localhost:4200")
public class VentasController {

    private final ProcesarVentaUseCase procesarVentaUseCase;

    public VentasController(ProcesarVentaUseCase procesarVentaUseCase) {
        this.procesarVentaUseCase = procesarVentaUseCase;
    }

    @PostMapping("/procesar")
    public ResponseEntity<VentaResponse> procesarVenta(@RequestBody VentaRequest request) {
        VentaResponse response = procesarVentaUseCase.procesarVenta(request);
        if ("ERROR".equals(response.getEstado())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/validar")
    public ResponseEntity<String> validarVenta(@Valid @RequestBody VentaRequest request) {
        procesarVentaUseCase.validarVenta(request);
        return ResponseEntity.ok("Venta válida — puede procesarse correctamente");
    }

    @GetMapping
    public ResponseEntity<List<Venta>> obtenerTodas() {
        return ResponseEntity.ok(procesarVentaUseCase.obtenerTodasLasVentas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Venta> obtenerPorId(@PathVariable Integer id) {
        return procesarVentaUseCase.buscarVentaPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Venta>> porUsuario(@PathVariable Integer userId) {
        return ResponseEntity.ok(procesarVentaUseCase.buscarVentasPorUsuario(userId));
    }

    @GetMapping("/today")
    public ResponseEntity<List<Venta>> deHoy() {
        return ResponseEntity.ok(procesarVentaUseCase.obtenerVentasDeHoy());
    }

    @GetMapping("/factura/{id}/pdf")
    public ResponseEntity<byte[]> descargarPdf(@PathVariable Long id) {
        byte[] pdf = procesarVentaUseCase.generarFacturaPdf(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "factura_" + id + ".pdf");
        headers.setContentLength(pdf.length);
        return ResponseEntity.ok().headers(headers).body(pdf);
    }
}