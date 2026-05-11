package com.sistemaventas.backend.infrastructure.persistence.adapter;

import com.sistemaventas.backend.domain.model.Producto;
import com.sistemaventas.backend.domain.ports.out.ProductoRepositoryPort;
import com.sistemaventas.backend.infrastructure.persistence.entity.ProductoJpaEntity;
import com.sistemaventas.backend.infrastructure.persistence.mapper.ProductoMapper;
import com.sistemaventas.backend.infrastructure.persistence.repository.ProductoJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador de salida — implementa ProductoRepositoryPort usando Spring Data JPA.
 * Es el único punto de contacto entre el dominio y la base de datos para Productos.
 */
@Component
public class ProductoRepositoryAdapter implements ProductoRepositoryPort {

    private final ProductoJpaRepository jpaRepository;
    private final ProductoMapper mapper;

    public ProductoRepositoryAdapter(ProductoJpaRepository jpaRepository, ProductoMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Producto> buscarPorId(Integer id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Producto guardar(Producto producto) {
        if (producto.getId() == null) {
            ProductoJpaEntity entity = mapper.toJpaEntity(producto);
            return mapper.toDomain(jpaRepository.save(entity));
        }
        // Con ID asignado, JPA hace merge sobre el objeto devuelto por MapStruct.
        // Ese merge puede poner NULL en columnas NOT NULL (p. ej. CATEGORIA) y fallar el INSERT/UPDATE.
        ProductoJpaEntity managed = jpaRepository.findById(producto.getId())
                .orElseThrow(() -> new IllegalStateException("Producto no encontrado: " + producto.getId()));
        managed.setCantidadDisponible(producto.getCantidadDisponible());
        managed.setPrecioUnitario(producto.getPrecioUnitario());
        managed.setDescripcion(producto.getDescripcion());
        managed.setCategoria(producto.getCategoria());
        return mapper.toDomain(jpaRepository.save(managed));
    }

    @Override
    public List<Producto> buscarTodos() {
        return mapper.toDomainList(jpaRepository.findAll());
    }

    @Override
    public void eliminar(Integer id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existePorId(Integer id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public List<Producto> buscarPorCategoria(String categoria) {
        return mapper.toDomainList(jpaRepository.findByCategoria(categoria));
    }

    @Override
    public List<Producto> buscarPorTermino(String termino) {
        return mapper.toDomainList(
                jpaRepository.findByDescripcionContainingIgnoreCaseOrCategoriaContainingIgnoreCase(termino, termino));
    }

    @Override
    public List<Producto> buscarConStockBajo(Integer limite) {
        return mapper.toDomainList(jpaRepository.findConStockBajo(limite));
    }

    @Override
    public List<String> buscarCategorias() {
        return jpaRepository.findDistinctCategorias();
    }
}
