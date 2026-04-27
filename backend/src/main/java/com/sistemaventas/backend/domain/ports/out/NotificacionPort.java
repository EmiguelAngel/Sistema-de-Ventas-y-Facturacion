package com.sistemaventas.backend.domain.ports.out;

import com.sistemaventas.backend.domain.model.Producto;

/**
 * Puerto de salida — contrato para enviar notificaciones de cambio de stock.
 * La implementación concreta vive en infrastructure/notification.
 */
public interface NotificacionPort {
    void notificarCambioStock(Producto producto, int stockAnterior, int nuevoStock);
}
