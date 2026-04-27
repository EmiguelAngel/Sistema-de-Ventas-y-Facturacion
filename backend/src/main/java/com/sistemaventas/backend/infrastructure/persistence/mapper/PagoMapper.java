package com.sistemaventas.backend.infrastructure.persistence.mapper;

import com.sistemaventas.backend.domain.model.PagoDomain;
import com.sistemaventas.backend.infrastructure.persistence.entity.PagoJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/** MapStruct Mapper — PagoJpaEntity ↔ PagoDomain. */
@Mapper(componentModel = "spring")
public interface PagoMapper {

    @Mapping(source = "idPago", target = "id")
    @Mapping(source = "factura.idFactura", target = "idFactura")
    PagoDomain toDomain(PagoJpaEntity entity);

    @Mapping(source = "id", target = "idPago")
    @Mapping(target = "factura", ignore = true)
    PagoJpaEntity toJpaEntity(PagoDomain domain);
}
