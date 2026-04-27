package com.sistemaventas.backend.infrastructure.persistence.mapper;

import com.sistemaventas.backend.domain.model.UsuarioDomain;
import com.sistemaventas.backend.infrastructure.persistence.entity.UsuarioJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/** MapStruct Mapper — UsuarioJpaEntity ↔ UsuarioDomain. */
@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    @Mapping(source = "idUsuario", target = "id")
    @Mapping(source = "rol.nombreRol", target = "nombreRol")
    UsuarioDomain toDomain(UsuarioJpaEntity entity);

    @Mapping(source = "id", target = "idUsuario")
    @Mapping(target = "rol", ignore = true)
    @Mapping(target = "facturas", ignore = true)
    UsuarioJpaEntity toJpaEntity(UsuarioDomain domain);

    List<UsuarioDomain> toDomainList(List<UsuarioJpaEntity> entities);
}
