package com.sistemaventas.backend.observer;

import com.sistemaventas.backend.domain.model.Producto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class InventarioNotificationService implements InventarioSubject {

    private static final Logger log = LoggerFactory.getLogger(InventarioNotificationService.class);

    private final List<InventarioObserver> observadores = new ArrayList<>();
    private static final int STOCK_MINIMO_DEFAULT = 10;
    private static final int STOCK_CRITICO = 5;

    public static int getSTOCK_MINIMO_DEFAULT() { return STOCK_MINIMO_DEFAULT; }

    public InventarioNotificationService(List<InventarioObserver> observers) {
        this.observadores.addAll(observers);
        log.info("InventarioNotificationService inicializado con {} observadores", observers.size());
    }

    @Override
    public void agregarObservador(InventarioObserver observer) {
        if (!observadores.contains(observer)) {
            observadores.add(observer);
            log.debug("Observador agregado: {}", observer.getClass().getSimpleName());
        }
    }

    @Override
    public void eliminarObservador(InventarioObserver observer) {
        observadores.remove(observer);
        log.debug("Observador eliminado: {}", observer.getClass().getSimpleName());
    }

    @Override
    public void notificarCambioStock(Producto producto, int stockAnterior, int nuevoStock) {
        log.info("Notificando cambio de stock — producto: '{}', {} -> {}",
                producto.getDescripcion(), stockAnterior, nuevoStock);

        for (InventarioObserver observer : observadores) {
            try {
                observer.onStockChange(producto, stockAnterior, nuevoStock);
            } catch (Exception e) {
                log.error("Error en observador {}: {}", observer.getClass().getSimpleName(), e.getMessage());
            }
        }

        if (nuevoStock == 0) {
            notificarProductoAgotado(producto);
        } else if (nuevoStock <= STOCK_CRITICO) {
            notificarStockBajo(producto, nuevoStock);
        } else if (stockAnterior <= STOCK_CRITICO && nuevoStock > STOCK_CRITICO) {
            notificarProductoRestockado(producto, nuevoStock);
        }
    }

    @Override
    public void notificarStockBajo(Producto producto, int stockActual) {
        log.warn("STOCK BAJO — {} (stock: {})", producto.getDescripcion(), stockActual);
        for (InventarioObserver observer : observadores) {
            try {
                observer.onStockBajo(producto, stockActual);
            } catch (Exception e) {
                log.error("Error en observador {}: {}", observer.getClass().getSimpleName(), e.getMessage());
            }
        }
    }

    @Override
    public void notificarProductoAgotado(Producto producto) {
        log.warn("PRODUCTO AGOTADO — {}", producto.getDescripcion());
        for (InventarioObserver observer : observadores) {
            try {
                observer.onProductoAgotado(producto);
            } catch (Exception e) {
                log.error("Error en observador {}: {}", observer.getClass().getSimpleName(), e.getMessage());
            }
        }
    }

    @Override
    public void notificarProductoRestockado(Producto producto, int nuevoStock) {
        log.info("PRODUCTO RESTOCKADO — {} (nuevo stock: {})", producto.getDescripcion(), nuevoStock);
        for (InventarioObserver observer : observadores) {
            try {
                observer.onProductoRestockado(producto, nuevoStock);
            } catch (Exception e) {
                log.error("Error en observador {}: {}", observer.getClass().getSimpleName(), e.getMessage());
            }
        }
    }

    public void procesarCambioStock(Producto producto, int stockAnterior) {
        notificarCambioStock(producto, stockAnterior, producto.getCantidadDisponible());
    }

    public List<String> obtenerObservadoresRegistrados() {
        return observadores.stream().map(obs -> obs.getClass().getSimpleName()).toList();
    }

    public List<InventarioObserver> getObservadores() {
        return observadores;
    }
}
