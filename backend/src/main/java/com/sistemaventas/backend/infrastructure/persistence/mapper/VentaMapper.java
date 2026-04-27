package com.sistemaventas.backend.infrastructure.persistence.mapper;

import com.sistemaventas.backend.domain.model.ItemVenta;
import com.sistemaventas.backend.domain.model.Venta;
import com.sistemaventas.backend.infrastructure.persistence.entity.DetalleFacturaJpaEntity;
import com.sistemaventas.backend.infrastructure.persistence.entity.FacturaJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * MapStruct Mapper — FacturaJpaEntity ↔ Venta (dominio).
 * Los ítems (detalles de factura) se mapean a ItemVenta del dominio.
 */
@Mapper(componentModel = "spring", uses = {ProductoMapper.class})
public interface VentaMapper {

    @Mapping(source = "idFactura", target = "id")
    @Mapping(source = "usuario.idUsuario", target = "idUsuario")
    @Mapping(source = "usuario.nombre", target = "nombreUsuario")
    @Mapping(source = "detallesFactura", target = "items")
    Venta toDomain(FacturaJpaEntity entity);

    @Mapping(source = "id", target = "idFactura")
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "pago", ignore = true)
    @Mapping(source = "items", target = "detallesFactura")
    FacturaJpaEntity toJpaEntity(Venta domain);

    List<Venta> toDomainList(List<FacturaJpaEntity> entities);

    @Mapping(source = "producto.idProducto", target = "idProducto")
    @Mapping(source = "producto.descripcion", target = "descripcionProducto")
    ItemVenta detalleToItem(DetalleFacturaJpaEntity detalle);

    @Mapping(source = "idProducto", target = "producto.idProducto")
    @Mapping(target = "factura", ignore = true)
    @Mapping(target = "idDetalle", ignore = true)
    DetalleFacturaJpaEntity itemToDetalle(ItemVenta item);
}
