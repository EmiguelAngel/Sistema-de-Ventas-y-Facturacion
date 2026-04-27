package com.sistemaventas.backend.observer;

import com.sistemaventas.backend.domain.model.Producto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PushNotificationObserver implements InventarioObserver {

    private static final Logger log = LoggerFactory.getLogger(PushNotificationObserver.class);

    @Override
    public void onStockChange(Producto producto, int stockAnterior, int nuevoStock) {
        int diferencia = Math.abs(nuevoStock - stockAnterior);
        if (diferencia >= 10) {
            log.info("[PUSH] Cambio significativo de stock en '{}' (delta: {})",
                    producto.getDescripcion(), nuevoStock - stockAnterior);
        }
    }

    @Override
    public void onStockBajo(Producto producto, int stockActual) {
        log.warn("[PUSH ALERTA] Stock bajo en '{}' ({} unidades)", producto.getDescripcion(), stockActual);
        enviarNotificacionPush("Stock Bajo", "Stock bajo en " + producto.getDescripcion());
    }

    @Override
    public void onProductoAgotado(Producto producto) {
        log.warn("[PUSH CRITICO] Producto agotado: '{}'", producto.getDescripcion());
        enviarNotificacionPush("Producto Agotado", producto.getDescripcion() + " está agotado");
        enviarSMS("Producto agotado: " + producto.getDescripcion());
    }

    @Override
    public void onProductoRestockado(Producto producto, int nuevoStock) {
        log.info("[PUSH INFO] Producto restockado: '{}' ({} unidades)", producto.getDescripcion(), nuevoStock);
        enviarNotificacionPush("Restock Exitoso", producto.getDescripcion() + " disponible nuevamente");
    }

    private void enviarNotificacionPush(String titulo, String mensaje) {
        log.debug("[PUSH] {} — {}", titulo, mensaje);
        // Integración real: Firebase, OneSignal, etc.
    }

    private void enviarSMS(String mensaje) {
        log.debug("[SMS] -> {}", mensaje);
        // Integración real: Twilio, AWS SNS, etc.
    }
}
