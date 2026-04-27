package com.sistemaventas.backend.infrastructure.persistence.mapper;

import com.sistemaventas.backend.domain.model.Producto;
import com.sistemaventas.backend.infrastructure.persistence.entity.ProductoJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * MapStruct Mapper — convierte entre ProductoJpaEntity (infraestructura) y Producto (dominio).
 * componentModel = "spring" hace que Spring gestione la instancia como un @Component.
 */
@Mapper(componentModel = "spring")
public interface ProductoMapper {

    @Mapping(source = "idProducto", target = "id")
    Producto toDomain(ProductoJpaEntity entity);

    @Mapping(source = "id", target = "idProducto")
    @Mapping(target = "detallesFactura", ignore = true)
    @Mapping(target = "inventarios", ignore = true)
    ProductoJpaEntity toJpaEntity(Producto domain);

    List<Producto> toDomainList(List<ProductoJpaEntity> entities);
}
