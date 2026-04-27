package com.sistemaventas.backend.infrastructure.notification;

import com.sistemaventas.backend.domain.model.Producto;
import com.sistemaventas.backend.domain.ports.out.NotificacionPort;
import com.sistemaventas.backend.observer.InventarioNotificationService;
import org.springframework.stereotype.Component;

/**
 * Adaptador de salida — conecta NotificacionPort con el patrón Observer existente.
 * Ahora ambas capas usan domain.model.Producto directamente, sin conversión.
 */
@Component
public class InventarioNotificationAdapter implements NotificacionPort {

    private final InventarioNotificationService notificationService;

    public InventarioNotificationAdapter(InventarioNotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Override
    public void notificarCambioStock(Producto producto, int stockAnterior, int nuevoStock) {
        notificationService.notificarCambioStock(producto, stockAnterior, nuevoStock);
    }
}
