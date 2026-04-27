package com.sistemaventas.backend.observer;

import com.sistemaventas.backend.domain.model.Producto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EmailNotificationObserver implements InventarioObserver {

    private static final Logger log = LoggerFactory.getLogger(EmailNotificationObserver.class);

    @Override
    public void onStockChange(Producto producto, int stockAnterior, int nuevoStock) {
        log.info("[EMAIL] Cambio de stock en '{}': {} -> {}", producto.getDescripcion(), stockAnterior, nuevoStock);
    }

    @Override
    public void onStockBajo(Producto producto, int stockActual) {
        log.warn("[EMAIL ALERTA] Stock bajo: '{}' — quedan {} unidades", producto.getDescripcion(), stockActual);
        log.info("[EMAIL] Enviando alerta a admin@sistemaventas.com — Asunto: Stock bajo en '{}'",
                producto.getDescripcion());
    }

    @Override
    public void onProductoAgotado(Producto producto) {
        log.warn("[EMAIL CRITICO] Producto agotado: '{}'", producto.getDescripcion());
        log.info("[EMAIL] Enviando alerta URGENTE a admin@sistemaventas.com, compras@sistemaventas.com");
    }

    @Override
    public void onProductoRestockado(Producto producto, int nuevoStock) {
        log.info("[EMAIL INFO] Producto restockado: '{}' (nuevo stock: {})", producto.getDescripcion(), nuevoStock);
    }
}
