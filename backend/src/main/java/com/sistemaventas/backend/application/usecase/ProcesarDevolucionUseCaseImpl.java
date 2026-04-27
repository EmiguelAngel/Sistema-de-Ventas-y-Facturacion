package com.sistemaventas.backend.application.usecase;

import com.sistemaventas.backend.domain.model.Producto;
import com.sistemaventas.backend.domain.ports.in.ProcesarDevolucionUseCase;
import com.sistemaventas.backend.domain.ports.out.ProductoRepositoryPort;
import com.sistemaventas.backend.dto.DevolucionDTO;
import com.sistemaventas.backend.dto.DevolucionRequestDTO;
import com.sistemaventas.backend.exception.ResourceNotFoundException;
import com.sistemaventas.backend.infrastructure.persistence.entity.DetalleFacturaJpaEntity;
import com.sistemaventas.backend.infrastructure.persistence.entity.DevolucionJpaEntity;
import com.sistemaventas.backend.infrastructure.persistence.entity.FacturaJpaEntity;
import com.sistemaventas.backend.infrastructure.persistence.repository.DetalleFacturaJpaRepository;
import com.sistemaventas.backend.infrastructure.persistence.repository.DevolucionJpaRepository;
import com.sistemaventas.backend.infrastructure.persistence.repository.FacturaJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.payment.PaymentRefundClient;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.PaymentRefund;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Caso de Uso — Procesamiento de Devoluciones.
 * Refactorizado desde DevolucionService: ahora usa el Puerto de dominio
 * en lugar de inyectar repositorios directamente.
 */
@Service
public class ProcesarDevolucionUseCaseImpl implements ProcesarDevolucionUseCase {

    private static final Logger log = LoggerFactory.getLogger(ProcesarDevolucionUseCaseImpl.class);

    private final FacturaJpaRepository facturaRepo;
    private final DetalleFacturaJpaRepository detalleRepo;
    private final DevolucionJpaRepository devolucionRepo;
    private final ProductoRepositoryPort productoPort;

    @Value("${mercadopago.access.token}")
    private String mercadoPagoAccessToken;

    public ProcesarDevolucionUseCaseImpl(FacturaJpaRepository facturaRepo,
                                         DetalleFacturaJpaRepository detalleRepo,
                                         DevolucionJpaRepository devolucionRepo,
                                         ProductoRepositoryPort productoPort) {
        this.facturaRepo = facturaRepo;
        this.detalleRepo = detalleRepo;
        this.devolucionRepo = devolucionRepo;
        this.productoPort = productoPort;
    }

    @Override
    @Transactional
    public DevolucionDTO procesarDevolucion(DevolucionRequestDTO request) {
        log.info("Procesando devolución para factura ID: {}", request.getIdFactura());

        // 1. Validar factura
        FacturaJpaEntity factura = facturaRepo.findById(request.getIdFactura())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Factura no encontrada con ID: " + request.getIdFactura()));

        if (Boolean.TRUE.equals(factura.getDevuelta())) {
            throw new IllegalStateException("Esta factura ya fue devuelta anteriormente");
        }

        boolean tienePagoMP = esPaymentIdValido(factura.getPaymentId());
        String refundId = null;

        // 2. Reembolso MercadoPago (solo si aplica)
        if (tienePagoMP) {
            log.info("Procesando reembolso MercadoPago para payment_id: {}", factura.getPaymentId());
            refundId = procesarReembolsoMercadoPago(factura.getPaymentId());
        }

        // 3. Restaurar inventario usando el Puerto de dominio (atómico)
        restaurarInventario(factura);

        // 4. Marcar factura como devuelta
        factura.setDevuelta(true);
        facturaRepo.save(factura);

        // 5. Registrar devolución
        DevolucionJpaEntity devolucion = DevolucionJpaEntity.builder()
                .factura(factura)
                .paymentId(factura.getPaymentId())
                .refundId(refundId)
                .montoDevuelto(factura.getTotal())
                .motivo(request.getMotivo())
                .estado("APROBADA")
                .fechaDevolucion(LocalDateTime.now())
                .usuarioDevolucion(request.getUsuarioDevolucion())
                .build();

        DevolucionJpaEntity guardada = devolucionRepo.save(devolucion);
        log.info("Devolución registrada con ID: {}", guardada.getIdDevolucion());
        return toDTO(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DevolucionDTO> obtenerTodas() {
        return devolucionRepo.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public DevolucionDTO buscarPorId(Long id) {
        return devolucionRepo.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Devolución no encontrada con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DevolucionDTO> buscarPorFactura(Integer idFactura) {
        return devolucionRepo.findByFactura_IdFactura(idFactura).stream()
                .map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DevolucionDTO> buscarPorEstado(String estado) {
        return devolucionRepo.findByEstado(estado).stream()
                .map(this::toDTO).collect(Collectors.toList());
    }

    // ── Privados ────────────────────────────────────────────────────────────────

    private void restaurarInventario(FacturaJpaEntity factura) {
        List<DetalleFacturaJpaEntity> detalles = detalleRepo.findByFactura_IdFactura(factura.getIdFactura());
        for (DetalleFacturaJpaEntity detalle : detalles) {
            Integer idProducto = detalle.getProducto().getIdProducto();
            Producto producto = productoPort.buscarPorId(idProducto)
                    .orElseThrow(() -> new ProductoNoEncontradoException(idProducto));
            producto.aumentarStock(detalle.getCantidad());
            productoPort.guardar(producto);
            log.info("Stock restaurado: producto ID {} +{}", idProducto, detalle.getCantidad());
        }
    }

    private String procesarReembolsoMercadoPago(String paymentId) {
        try {
            MercadoPagoConfig.setAccessToken(mercadoPagoAccessToken);
            PaymentRefundClient client = new PaymentRefundClient();
            PaymentRefund refund = client.refund(Long.valueOf(paymentId));
            log.info("Reembolso MercadoPago exitoso. Refund ID: {}", refund.getId());
            return refund.getId().toString();
        } catch (MPException | MPApiException e) {
            log.error("Error reembolso MercadoPago: {}", e.getMessage());
            throw new RuntimeException("Error al procesar el reembolso: " + e.getMessage(), e);
        }
    }

    private boolean esPaymentIdValido(String paymentId) {
        if (paymentId == null || paymentId.isBlank()) return false;
        if (paymentId.startsWith("TEST_") || paymentId.startsWith("test_")) return false;
        return paymentId.chars().allMatch(Character::isDigit);
    }

    private DevolucionDTO toDTO(DevolucionJpaEntity d) {
        return DevolucionDTO.builder()
                .idDevolucion(d.getIdDevolucion())
                .idFactura(d.getFactura().getIdFactura())
                .numeroFactura("FAC-" + d.getFactura().getIdFactura())
                .paymentId(d.getPaymentId())
                .refundId(d.getRefundId())
                .montoDevuelto(d.getMontoDevuelto())
                .motivo(d.getMotivo())
                .estado(d.getEstado())
                .fechaDevolucion(d.getFechaDevolucion())
                .usuarioDevolucion(d.getUsuarioDevolucion())
                .build();
    }

    // Clase de excepción privada reutilizada
    private static class ProductoNoEncontradoException extends RuntimeException {
        ProductoNoEncontradoException(Integer id) { super("Producto no encontrado: " + id); }
    }
}
