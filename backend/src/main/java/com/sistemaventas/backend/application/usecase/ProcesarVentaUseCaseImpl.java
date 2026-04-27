package com.sistemaventas.backend.application.usecase;

import com.sistemaventas.backend.domain.exception.FacturaNoEncontradaException;
import com.sistemaventas.backend.domain.exception.ProductoNoEncontradoException;
import com.sistemaventas.backend.domain.exception.UsuarioNoEncontradoException;
import com.sistemaventas.backend.domain.model.ItemVenta;
import com.sistemaventas.backend.domain.model.PagoDomain;
import com.sistemaventas.backend.domain.model.Producto;
import com.sistemaventas.backend.domain.model.UsuarioDomain;
import com.sistemaventas.backend.domain.model.Venta;
import com.sistemaventas.backend.domain.ports.in.ProcesarVentaUseCase;
import com.sistemaventas.backend.domain.ports.out.NotificacionPort;
import com.sistemaventas.backend.domain.ports.out.PagoRepositoryPort;
import com.sistemaventas.backend.domain.ports.out.ProductoRepositoryPort;
import com.sistemaventas.backend.domain.ports.out.UsuarioRepositoryPort;
import com.sistemaventas.backend.domain.ports.out.VentaRepositoryPort;
import com.sistemaventas.backend.dto.request.VentaRequest;
import com.sistemaventas.backend.dto.response.VentaResponse;
import com.sistemaventas.backend.service.FacturaPdfService;
import com.sistemaventas.backend.infrastructure.persistence.entity.DetalleFacturaJpaEntity;
import com.sistemaventas.backend.infrastructure.persistence.entity.FacturaJpaEntity;
import com.sistemaventas.backend.infrastructure.persistence.repository.FacturaJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Caso de Uso — Procesamiento de Ventas.
 *
 * CORRECCIÓN CRÍTICA DE ATOMICIDAD:
 * El flujo anterior guardaba la Factura (paso 4) y solo DESPUÉS reducía el stock (paso 7).
 * Si el paso 7 fallaba, la factura quedaba persistida pero el inventario no se actualizaba.
 *
 * Nuevo flujo atómico (todo en una sola transacción @Transactional):
 *   1. Validar usuario
 *   2. Validar productos y stock
 *   3. REDUCIR STOCK (dentro de la misma transacción)
 *   4. Crear y persistir la Venta con todos sus detalles
 *   5. Registrar Pago
 *   6. Notificar observadores (no bloquea el rollback)
 *   → Si cualquier paso falla: rollback completo — stock vuelve a su estado original.
 */
@Service
public class ProcesarVentaUseCaseImpl implements ProcesarVentaUseCase {

    private static final Logger log = LoggerFactory.getLogger(ProcesarVentaUseCaseImpl.class);

    private final ProductoRepositoryPort productoPort;
    private final UsuarioRepositoryPort usuarioPort;
    private final VentaRepositoryPort ventaPort;
    private final PagoRepositoryPort pagoPort;
    private final NotificacionPort notificacionPort;
    private final FacturaPdfService facturaPdfService;
    private final FacturaJpaRepository facturaJpaRepository;

    public ProcesarVentaUseCaseImpl(ProductoRepositoryPort productoPort,
                                    UsuarioRepositoryPort usuarioPort,
                                    VentaRepositoryPort ventaPort,
                                    PagoRepositoryPort pagoPort,
                                    NotificacionPort notificacionPort,
                                    FacturaPdfService facturaPdfService,
                                    FacturaJpaRepository facturaJpaRepository) {
        this.productoPort = productoPort;
        this.usuarioPort = usuarioPort;
        this.ventaPort = ventaPort;
        this.pagoPort = pagoPort;
        this.notificacionPort = notificacionPort;
        this.facturaPdfService = facturaPdfService;
        this.facturaJpaRepository = facturaJpaRepository;
    }

    @Override
    @Transactional
    public VentaResponse procesarVenta(VentaRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("La solicitud de venta es obligatoria");
        }
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("La venta debe incluir al menos un item");
        }
        log.info("Iniciando procesamiento de venta — usuario: {}, items: {}",
                request.getIdUsuario(), request.getItems().size());

        // 1. Validar usuario
        UsuarioDomain usuario = usuarioPort.buscarPorId(request.getIdUsuario())
                .orElseThrow(() -> new UsuarioNoEncontradoException(request.getIdUsuario()));

        // 2. Validar stock y construir ítems (sin modificar BD aún)
        List<ItemConProducto> itemsValidados = validarItemsYStock(request.getItems());

        // 3. REDUCIR STOCK PRIMERO — dentro de esta transacción
        //    Si cualquier reducción falla, el rollback revierte TODO.
        itemsValidados.forEach(iv -> {
            iv.producto.reducirStock(iv.cantidad); // Lanza StockInsuficienteException si no alcanza
            productoPort.guardar(iv.producto);
            log.info("Stock reducido: producto '{}' -> nuevo stock: {}",
                    iv.producto.getDescripcion(), iv.producto.getCantidadDisponible());
        });

        // 4. Construir la Venta de dominio y calcular totales
        Venta venta = construirVenta(usuario, itemsValidados, request);

        // 5. Persistir la Venta (con sus DetalleFactura en cascada)
        Venta ventaGuardada = ventaPort.guardar(venta);
        log.info("Venta persistida con ID: {}", ventaGuardada.getId());

        // 6. Registrar Pago
        PagoDomain pago = registrarPago(request.getDatosPago(), ventaGuardada.getTotal(), ventaGuardada.getId());
        log.info("Pago registrado — método: {}", pago.getMetodoPago());

        // 7. Notificar observadores (no crítico — no provoca rollback si falla)
        itemsValidados.forEach(iv ->
                notificacionPort.notificarCambioStock(iv.producto,
                        iv.stockAnterior, iv.producto.getCantidadDisponible()));

        // 8. Construir respuesta usando entidad JPA para el VentaResponse existente
        return buildResponse(ventaGuardada.getId(), pago);
    }

    @Override
    @Transactional(readOnly = true)
    public void validarVenta(VentaRequest request) {
        usuarioPort.buscarPorId(request.getIdUsuario())
                .orElseThrow(() -> new UsuarioNoEncontradoException(request.getIdUsuario()));
        validarItemsYStock(request.getItems());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Venta> obtenerTodasLasVentas() {
        return ventaPort.buscarTodas();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Venta> buscarVentaPorId(Integer id) {
        return ventaPort.buscarPorId(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Venta> buscarVentasPorUsuario(Integer idUsuario) {
        return ventaPort.buscarPorUsuario(idUsuario);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Venta> obtenerVentasDeHoy() {
        return ventaPort.buscarDeHoy();
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] generarFacturaPdf(Long id) {
        FacturaJpaEntity factura = facturaJpaRepository.findById(id.intValue())
                .orElseThrow(() -> new FacturaNoEncontradaException(id.intValue()));
        try {
            return facturaPdfService.generarFacturaPdf(toLegacyFactura(factura));
        } catch (Exception e) {
            throw new RuntimeException("Error generando PDF para factura " + id + ": " + e.getMessage(), e);
        }
    }

    // ── Métodos privados ───────────────────────────────────────────────────────

    private List<ItemConProducto> validarItemsYStock(List<VentaRequest.ItemVenta> items) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("La venta debe tener al menos un item");
        }
        List<ItemConProducto> resultado = new ArrayList<>();
        for (VentaRequest.ItemVenta item : items) {
            Producto producto = productoPort.buscarPorId(item.getIdProducto())
                    .orElseThrow(() -> new ProductoNoEncontradoException(item.getIdProducto()));
            if (producto.getPrecioUnitario() == null ||
                    producto.getPrecioUnitario().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException(
                        "El producto '" + producto.getDescripcion() + "' no tiene precio válido");
            }
            if (!producto.tieneStockSuficiente(item.getCantidad())) {
                throw new com.sistemaventas.backend.domain.exception.StockInsuficienteException(
                        producto.getDescripcion(), producto.getCantidadDisponible(), item.getCantidad());
            }
            int stockAnterior = producto.getCantidadDisponible();
            resultado.add(new ItemConProducto(producto, item.getCantidad(), stockAnterior));
        }
        return resultado;
    }

    private Venta construirVenta(UsuarioDomain usuario, List<ItemConProducto> items, VentaRequest request) {
        Venta venta = new Venta();
        venta.setIdUsuario(usuario.getId());
        venta.setNombreUsuario(usuario.getNombre());
        venta.setFecha(new Date());
        if (request.getPaymentId() != null && !request.getPaymentId().isBlank()) {
            venta.setPaymentId(request.getPaymentId());
        }
        List<ItemVenta> itemsDominio = items.stream()
                .map(iv -> new ItemVenta(iv.producto.getId(), iv.producto.getDescripcion(),
                        iv.cantidad, iv.producto.getPrecioUnitario()))
                .toList();
        venta.setItems(itemsDominio); // setItems llama a calcularTotales()
        return venta;
    }

    private PagoDomain registrarPago(VentaRequest.DatosPago datosPago, BigDecimal total, Integer idFactura) {
        if (datosPago == null || datosPago.getMetodoPago() == null || datosPago.getMetodoPago().isBlank()) {
            throw new IllegalArgumentException("Los datos de pago son obligatorios");
        }
        PagoDomain pago = new PagoDomain();
        pago.setIdFactura(idFactura);
        pago.setMetodoPago(datosPago.getMetodoPago());
        pago.setMonto(total);
        if (datosPago.getNombreTitular() != null) {
            pago.setNombreTitular(datosPago.getNombreTitular());
        }
        if (datosPago.getNumeroTarjeta() != null && datosPago.getNumeroTarjeta().length() >= 4) {
            String numero = datosPago.getNumeroTarjeta().replaceAll("\\s", "");
            pago.setNumeroTarjeta("****" + numero.substring(numero.length() - 4));
        }
        return pagoPort.guardar(pago);
    }

    private VentaResponse buildResponse(Integer idFactura, PagoDomain pago) {
        // Recargamos la entidad JPA para usarla en VentaResponse (compatibilidad con DTO existente)
        FacturaJpaEntity facturaEntity = facturaJpaRepository.findById(idFactura)
                .orElseThrow(() -> new FacturaNoEncontradaException(idFactura));

        // Adaptar PagoDomain a la entidad JPA Pago legacy para VentaResponse
        com.sistemaventas.backend.entity.Pago legacyPago = new com.sistemaventas.backend.entity.Pago();
        legacyPago.setIdPago(pago.getId());
        legacyPago.setMetodoPago(pago.getMetodoPago());
        legacyPago.setMonto(pago.getMonto());

        // Adaptar FacturaJpaEntity a Factura legacy para VentaResponse
        legacyPago.setNombreTitular(pago.getNombreTitular());
        legacyPago.setNumeroTarjeta(pago.getNumeroTarjeta());

        com.sistemaventas.backend.entity.Factura legacyFactura = toLegacyFactura(facturaEntity);
        return new VentaResponse(legacyFactura, legacyPago);
    }

    private com.sistemaventas.backend.entity.Factura toLegacyFactura(FacturaJpaEntity e) {
        com.sistemaventas.backend.entity.Factura f = new com.sistemaventas.backend.entity.Factura();
        f.setIdFactura(e.getIdFactura());
        f.setFecha(e.getFecha());
        f.setSubtotal(e.getSubtotal());
        f.setIva(e.getIva());
        f.setTotal(e.getTotal());
        f.setIdPago(e.getIdPago());
        if (e.getUsuario() != null) {
            com.sistemaventas.backend.entity.Usuario usuario = new com.sistemaventas.backend.entity.Usuario();
            usuario.setIdUsuario(e.getUsuario().getIdUsuario());
            usuario.setNombre(e.getUsuario().getNombre());
            usuario.setCorreo(e.getUsuario().getCorreo());
            f.setUsuario(usuario);
        }

        List<com.sistemaventas.backend.entity.DetalleFactura> detalles = new ArrayList<>();
        if (e.getDetallesFactura() != null) {
            for (DetalleFacturaJpaEntity d : e.getDetallesFactura()) {
                com.sistemaventas.backend.entity.DetalleFactura legacyDetalle =
                        new com.sistemaventas.backend.entity.DetalleFactura();
                legacyDetalle.setIdDetalle(d.getIdDetalle());
                legacyDetalle.setCantidad(d.getCantidad());
                legacyDetalle.setPrecioUnitario(d.getPrecioUnitario());
                legacyDetalle.setSubtotal(d.getSubtotal());

                if (d.getProducto() != null) {
                    com.sistemaventas.backend.entity.Producto legacyProducto =
                            new com.sistemaventas.backend.entity.Producto();
                    legacyProducto.setIdProducto(d.getProducto().getIdProducto());
                    legacyProducto.setDescripcion(d.getProducto().getDescripcion());
                    legacyProducto.setPrecioUnitario(d.getProducto().getPrecioUnitario());
                    legacyProducto.setCantidadDisponible(d.getProducto().getCantidadDisponible());
                    legacyProducto.setCategoria(d.getProducto().getCategoria());
                    legacyDetalle.setProducto(legacyProducto);
                }

                legacyDetalle.setFactura(f);
                detalles.add(legacyDetalle);
            }
        }
        f.setDetallesFactura(detalles);
        return f;
    }

    // ── Clase interna de apoyo ─────────────────────────────────────────────────

    private static class ItemConProducto {
        final Producto producto;
        final Integer cantidad;
        final int stockAnterior;

        ItemConProducto(Producto producto, Integer cantidad, int stockAnterior) {
            this.producto = producto;
            this.cantidad = cantidad;
            this.stockAnterior = stockAnterior;
        }
    }
}
