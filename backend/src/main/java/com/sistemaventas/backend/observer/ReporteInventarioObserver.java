package com.sistemaventas.backend.observer;

import com.sistemaventas.backend.domain.model.Producto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ReporteInventarioObserver implements InventarioObserver {

    private static final Logger log = LoggerFactory.getLogger(ReporteInventarioObserver.class);

    @Override
    public void onStockChange(Producto producto, int stockAnterior, int nuevoStock) {
        log.info("[REPORTE] Cambio inventario — producto: '{}' (ID: {}), stock: {} -> {}, variacion: {}",
                producto.getDescripcion(), producto.getId(),
                stockAnterior, nuevoStock, nuevoStock - stockAnterior);
    }

    @Override
    public void onStockBajo(Producto producto, int stockActual) {
        log.warn("[REPORTE ALERTA] Stock bajo — '{}' (ID: {}), stock actual: {}, estado: REQUIERE RESTOCK",
                producto.getDescripcion(), producto.getId(), stockActual);
    }

    @Override
    public void onProductoAgotado(Producto producto) {
        log.warn("[REPORTE CRITICO] Producto agotado — '{}' (ID: {}), estado: AGOTADO",
                producto.getDescripcion(), producto.getId());
    }

    @Override
    public void onProductoRestockado(Producto producto, int nuevoStock) {
        log.info("[REPORTE INFO] Producto restockado — '{}' (ID: {}), nuevo stock: {}, estado: DISPONIBLE",
                producto.getDescripcion(), producto.getId(), nuevoStock);
    }
}
