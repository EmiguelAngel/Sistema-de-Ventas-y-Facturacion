package com.sistemaventas.backend.controller;

import com.sistemaventas.backend.domain.model.Producto;
import com.sistemaventas.backend.domain.ports.in.ConsultarProductoUseCase;
import com.sistemaventas.backend.domain.ports.in.CrearProductoUseCase;
import com.sistemaventas.backend.dto.request.ProductoRequest;
import com.sistemaventas.backend.dto.response.ApiResponse;
import com.sistemaventas.backend.dto.response.ProductoResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST de Productos — adaptador de entrada HTTP.
 * Solo contiene lógica HTTP: deserialización, mapeo DTO↔dominio y códigos de estado.
 * Toda la lógica de negocio delega a los Use Cases via interfaces de puerto.
 */
@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "http://localhost:4200")
public class ProductoController {

    private final CrearProductoUseCase crearUseCase;
    private final ConsultarProductoUseCase consultarUseCase;

    public ProductoController(CrearProductoUseCase crearUseCase,
                              ConsultarProductoUseCase consultarUseCase) {
        this.crearUseCase = crearUseCase;
        this.consultarUseCase = consultarUseCase;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductoResponse>>> obtenerTodos() {
        List<ProductoResponse> response = consultarUseCase.obtenerTodos().stream()
                .map(this::toResponse).toList();
        return ResponseEntity.ok(new ApiResponse<>(true, "Productos obtenidos exitosamente", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductoResponse>> obtenerPorId(@PathVariable Integer id) {
        return consultarUseCase.buscarPorId(id)
                .map(p -> ResponseEntity.ok(new ApiResponse<>(true, "Producto encontrado", toResponse(p))))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Producto no encontrado", null)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProductoResponse>> crear(@Valid @RequestBody ProductoRequest request) {
        Producto creado = crearUseCase.crearProducto(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Producto creado exitosamente", toResponse(creado)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductoResponse>> actualizar(@PathVariable Integer id,
                                                                    @Valid @RequestBody ProductoRequest request) {
        Producto actualizado = crearUseCase.actualizarProducto(id, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Producto actualizado", toResponse(actualizado)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        return crearUseCase.eliminarProducto(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<ProductoResponse>> porCategoria(@PathVariable String categoria) {
        return ResponseEntity.ok(consultarUseCase.buscarPorCategoria(categoria).stream()
                .map(this::toResponse).toList());
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductoResponse>> buscar(@RequestParam String q) {
        return ResponseEntity.ok(consultarUseCase.buscarPorTermino(q).stream()
                .map(this::toResponse).toList());
    }

    @GetMapping("/stock-bajo")
    public ResponseEntity<List<ProductoResponse>> stockBajo(
            @RequestParam(defaultValue = "5") int limite) {
        return ResponseEntity.ok(consultarUseCase.buscarConStockBajo(limite).stream()
                .map(this::toResponse).toList());
    }

    @GetMapping("/categorias")
    public ResponseEntity<List<String>> categorias() {
        return ResponseEntity.ok(consultarUseCase.obtenerCategorias());
    }

    // ── Mapper HTTP ─────────────────────────────────────────────────────────────

    private ProductoResponse toResponse(Producto p) {
        return new ProductoResponse(p.getId(), p.getDescripcion(),
                p.getPrecioUnitario(), p.getCantidadDisponible(), p.getCategoria());
    }
}
