package com.sistemaventas.backend.application.usecase;

import com.sistemaventas.backend.domain.exception.ProductoNoEncontradoException;
import com.sistemaventas.backend.domain.model.Producto;
import com.sistemaventas.backend.domain.ports.in.ConsultarProductoUseCase;
import com.sistemaventas.backend.domain.ports.in.CrearProductoUseCase;
import com.sistemaventas.backend.domain.ports.out.NotificacionPort;
import com.sistemaventas.backend.domain.ports.out.ProductoRepositoryPort;
import com.sistemaventas.backend.dto.request.ProductoRequest;
import com.sistemaventas.backend.factory.ProductoFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Caso de Uso — Gestión de Productos.
 * Implementa CrearProductoUseCase y ConsultarProductoUseCase.
 *
 * Correcciones respecto al código anterior:
 * - Validaciones consolidadas (no duplicadas en Factory y Service).
 * - System.out.println reemplazado por SLF4J.
 * - @Transactional solo donde hay escritura en BD.
 * - ID generado por la BD vía @GeneratedValue; si no se provee ID en el request se guarda sin uno.
 */
@Service
public class ProductoUseCaseImpl implements CrearProductoUseCase, ConsultarProductoUseCase {

    private static final Logger log = LoggerFactory.getLogger(ProductoUseCaseImpl.class);

    private final ProductoRepositoryPort repositoryPort;
    private final NotificacionPort notificacionPort;

    public ProductoUseCaseImpl(ProductoRepositoryPort repositoryPort,
                               NotificacionPort notificacionPort) {
        this.repositoryPort = repositoryPort;
        this.notificacionPort = notificacionPort;
    }

    // ── Escrituras ─────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public Producto crearProducto(ProductoRequest request) {
        log.info("Creando producto: descripcion={}, categoria={}", request.getDescripcion(), request.getCategoria());
        validarRequest(request);

        // Verificar ID duplicado solo si se provee uno explícito
        if (request.getIdProducto() != null && request.getIdProducto() > 0) {
            if (repositoryPort.existePorId(request.getIdProducto())) {
                throw new IllegalArgumentException("Ya existe un producto con ID: " + request.getIdProducto());
            }
        }

        // Factory Method Pattern — delega creación por categoría, retorna domain.model.Producto
        ProductoFactory factory = ProductoFactory.obtenerFactory(request.getCategoria());
        Producto domainProducto = factory.crearProducto(request);

        Producto guardado = repositoryPort.guardar(domainProducto);
        log.info("Producto creado con ID: {}", guardado.getId());
        return guardado;
    }

    @Override
    @Transactional
    public Producto actualizarProducto(Integer id, ProductoRequest request) {
        log.info("Actualizando producto ID: {}", id);
        if (!repositoryPort.existePorId(id)) {
            throw new ProductoNoEncontradoException(id);
        }
        validarRequest(request);
        request.setIdProducto(id);

        ProductoFactory factory = ProductoFactory.obtenerFactory(request.getCategoria());
        Producto domainProducto = factory.crearProducto(request);
        domainProducto.setId(id);
        Producto actualizado = repositoryPort.guardar(domainProducto);
        log.info("Producto ID {} actualizado exitosamente", id);
        return actualizado;
    }

    @Override
    @Transactional
    public Producto actualizarStock(Integer id, Integer nuevaCantidad) {
        log.info("Actualizando stock producto ID: {} -> {}", id, nuevaCantidad);
        Producto producto = repositoryPort.buscarPorId(id)
                .orElseThrow(() -> new ProductoNoEncontradoException(id));
        int stockAnterior = producto.getCantidadDisponible() != null ? producto.getCantidadDisponible() : 0;
        producto.setCantidadDisponible(nuevaCantidad);
        Producto actualizado = repositoryPort.guardar(producto);
        notificacionPort.notificarCambioStock(actualizado, stockAnterior, nuevaCantidad);
        return actualizado;
    }

    @Override
    @Transactional
    public Producto reducirStock(Integer id, Integer cantidad) {
        log.info("Reduciendo stock producto ID: {} en {}", id, cantidad);
        Producto producto = repositoryPort.buscarPorId(id)
                .orElseThrow(() -> new ProductoNoEncontradoException(id));
        int stockAnterior = producto.getCantidadDisponible() != null ? producto.getCantidadDisponible() : 0;
        producto.reducirStock(cantidad); // Lanza StockInsuficienteException si no hay stock
        Producto actualizado = repositoryPort.guardar(producto);
        notificacionPort.notificarCambioStock(actualizado, stockAnterior, actualizado.getCantidadDisponible());
        return actualizado;
    }

    @Override
    @Transactional
    public Producto aumentarStock(Integer id, Integer cantidad) {
        log.info("Aumentando stock producto ID: {} en {}", id, cantidad);
        Producto producto = repositoryPort.buscarPorId(id)
                .orElseThrow(() -> new ProductoNoEncontradoException(id));
        int stockAnterior = producto.getCantidadDisponible() != null ? producto.getCantidadDisponible() : 0;
        producto.aumentarStock(cantidad);
        Producto actualizado = repositoryPort.guardar(producto);
        notificacionPort.notificarCambioStock(actualizado, stockAnterior, actualizado.getCantidadDisponible());
        return actualizado;
    }

    @Override
    @Transactional
    public boolean eliminarProducto(Integer id) {
        log.info("Eliminando producto ID: {}", id);
        if (!repositoryPort.existePorId(id)) {
            log.warn("Producto ID {} no encontrado para eliminar", id);
            return false;
        }
        repositoryPort.eliminar(id);
        log.info("Producto ID {} eliminado", id);
        return true;
    }

    // ── Lecturas ────────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public List<Producto> obtenerTodos() {
        return repositoryPort.buscarTodos();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Producto> buscarPorId(Integer id) {
        return repositoryPort.buscarPorId(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> buscarPorCategoria(String categoria) {
        return repositoryPort.buscarPorCategoria(categoria);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> buscarPorTermino(String termino) {
        return repositoryPort.buscarPorTermino(termino);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> buscarConStockBajo(Integer limite) {
        return repositoryPort.buscarConStockBajo(limite);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> obtenerCategorias() {
        return repositoryPort.buscarCategorias();
    }

    // ── Validaciones privadas ──────────────────────────────────────────────────

    private void validarRequest(ProductoRequest request) {
        if (request == null) throw new IllegalArgumentException("El request no puede ser nulo");
        if (request.getDescripcion() == null || request.getDescripcion().isBlank())
            throw new IllegalArgumentException("La descripción del producto es obligatoria");
        if (request.getPrecioUnitario() == null || request.getPrecioUnitario().doubleValue() <= 0)
            throw new IllegalArgumentException("El precio unitario debe ser mayor a 0");
        if (request.getCantidadDisponible() == null || request.getCantidadDisponible() < 0)
            throw new IllegalArgumentException("La cantidad disponible no puede ser negativa");
        if (request.getCategoria() == null || request.getCategoria().isBlank())
            throw new IllegalArgumentException("La categoría es obligatoria");
    }
}
