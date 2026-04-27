package com.sistemaventas.backend.factory;

import com.sistemaventas.backend.domain.model.Producto;
import com.sistemaventas.backend.dto.request.ProductoRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract Factory — crea instancias de domain.model.Producto
 * con reglas de negocio específicas por categoría.
 */
public abstract class ProductoFactory {

    protected static final Logger log = LoggerFactory.getLogger(ProductoFactory.class);

    public abstract Producto crearProducto(ProductoRequest request);

    public static ProductoFactory obtenerFactory(String categoria) {
        if (categoria == null || categoria.trim().isEmpty()) {
            return new ProductoGeneralFactory();
        }
        return switch (categoria.toLowerCase().trim()) {
            case "granos" -> new GranosProductoFactory();
            case "aceites" -> new AceitesProductoFactory();
            case "lácteos", "lacteos" -> new LacteosProductoFactory();
            case "panadería", "panaderia" -> new PanaderiaProductoFactory();
            case "endulzantes" -> new EndulzantesProductoFactory();
            default -> new ProductoGeneralFactory();
        };
    }

    protected void validarProductoBase(ProductoRequest request) {
        if (request == null) throw new IllegalArgumentException("El request no puede ser null");
        if (request.getDescripcion() == null || request.getDescripcion().trim().isEmpty())
            throw new IllegalArgumentException("La descripción del producto es obligatoria");
        if (request.getPrecioUnitario() == null || request.getPrecioUnitario().doubleValue() <= 0)
            throw new IllegalArgumentException("El precio unitario debe ser mayor a 0");
        if (request.getCantidadDisponible() == null || request.getCantidadDisponible() < 0)
            throw new IllegalArgumentException("La cantidad disponible no puede ser negativa");
        if (request.getCategoria() == null || request.getCategoria().trim().isEmpty())
            throw new IllegalArgumentException("La categoría es obligatoria");
    }

    protected Producto crearProductoBase(ProductoRequest request) {
        validarProductoBase(request);
        return new Producto(
                request.getIdProducto(),
                request.getDescripcion(),
                request.getPrecioUnitario(),
                request.getCantidadDisponible(),
                request.getCategoria());
    }
}

class GranosProductoFactory extends ProductoFactory {
    @Override
    public Producto crearProducto(ProductoRequest request) {
        Producto producto = crearProductoBase(request);
        producto.setCategoria(request.getCategoria());
        if (producto.getDescripcion().toLowerCase().contains("arroz")
                && producto.getPrecioUnitario().doubleValue() < 1000) {
            throw new IllegalArgumentException("El arroz debe tener un precio mínimo de $1000");
        }
        if (producto.getPrecioUnitario().doubleValue() > 3000) {
            log.debug("Grano premium detectado: {}", producto.getDescripcion());
        }
        return producto;
    }
}

class AceitesProductoFactory extends ProductoFactory {
    @Override
    public Producto crearProducto(ProductoRequest request) {
        Producto producto = crearProductoBase(request);
        producto.setCategoria("Aceites");
        if (producto.getPrecioUnitario().doubleValue() < 2000) {
            throw new IllegalArgumentException("Los aceites deben tener un precio mínimo de $2000");
        }
        log.debug("Aceite registrado (requiere almacenamiento fresco/seco): {}", producto.getDescripcion());
        return producto;
    }
}

class LacteosProductoFactory extends ProductoFactory {
    @Override
    public Producto crearProducto(ProductoRequest request) {
        Producto producto = crearProductoBase(request);
        producto.setCategoria("Lácteos");
        log.debug("Producto lácteo registrado (requiere refrigeración): {}", producto.getDescripcion());
        if (producto.getCantidadDisponible() > 100) {
            log.warn("Gran cantidad de producto perecedero en inventario: {}", producto.getDescripcion());
        }
        return producto;
    }
}

class PanaderiaProductoFactory extends ProductoFactory {
    @Override
    public Producto crearProducto(ProductoRequest request) {
        Producto producto = crearProductoBase(request);
        producto.setCategoria("Panadería");
        log.debug("Producto de panadería (vida útil corta): {}", producto.getDescripcion());
        if (producto.getCantidadDisponible() > 50) {
            log.warn("Cantidad alta para panadería (vida útil corta): {}", producto.getDescripcion());
        }
        return producto;
    }
}

class EndulzantesProductoFactory extends ProductoFactory {
    @Override
    public Producto crearProducto(ProductoRequest request) {
        Producto producto = crearProductoBase(request);
        producto.setCategoria("Endulzantes");
        log.debug("Endulzante registrado (proteger de humedad): {}", producto.getDescripcion());
        String desc = producto.getDescripcion().toLowerCase();
        if ((desc.contains("azúcar") || desc.contains("azucar"))
                && producto.getPrecioUnitario().doubleValue() < 800) {
            throw new IllegalArgumentException("El azúcar debe tener un precio mínimo de $800");
        }
        return producto;
    }
}

class ProductoGeneralFactory extends ProductoFactory {
    @Override
    public Producto crearProducto(ProductoRequest request) {
        Producto producto = crearProductoBase(request);
        if (request.getCategoria() == null || request.getCategoria().trim().isEmpty()) {
            producto.setCategoria("General");
        } else {
            producto.setCategoria(request.getCategoria());
        }
        log.debug("Producto creado con factory general: {}", producto.getDescripcion());
        return producto;
    }
}
