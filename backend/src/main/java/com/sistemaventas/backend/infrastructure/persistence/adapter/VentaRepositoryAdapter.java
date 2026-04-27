package com.sistemaventas.backend.infrastructure.persistence.adapter;

import com.sistemaventas.backend.domain.model.Venta;
import com.sistemaventas.backend.domain.ports.out.VentaRepositoryPort;
import com.sistemaventas.backend.infrastructure.persistence.entity.FacturaJpaEntity;
import com.sistemaventas.backend.infrastructure.persistence.entity.UsuarioJpaEntity;
import com.sistemaventas.backend.infrastructure.persistence.mapper.VentaMapper;
import com.sistemaventas.backend.infrastructure.persistence.repository.FacturaJpaRepository;
import com.sistemaventas.backend.infrastructure.persistence.repository.UsuarioJpaRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/** Adaptador de salida para Ventas (Facturas). */
@Component
public class VentaRepositoryAdapter implements VentaRepositoryPort {

    private final FacturaJpaRepository facturaRepo;
    private final UsuarioJpaRepository usuarioRepo;
    private final VentaMapper mapper;

    public VentaRepositoryAdapter(FacturaJpaRepository facturaRepo,
                                  UsuarioJpaRepository usuarioRepo,
                                  VentaMapper mapper) {
        this.facturaRepo = facturaRepo;
        this.usuarioRepo = usuarioRepo;
        this.mapper = mapper;
    }

    @Override
    public Venta guardar(Venta venta) {
        FacturaJpaEntity entity = mapper.toJpaEntity(venta);
        // Resolver la entidad Usuario desde la BD
        if (venta.getIdUsuario() != null) {
            UsuarioJpaEntity usuario = usuarioRepo.findById(venta.getIdUsuario())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Usuario no encontrado: " + venta.getIdUsuario()));
            entity.setUsuario(usuario);
        }
        // Vincular detalles con la factura (bidireccional)
        if (entity.getDetallesFactura() != null) {
            entity.getDetallesFactura().forEach(d -> d.setFactura(entity));
        }
        return mapper.toDomain(facturaRepo.save(entity));
    }

    @Override
    public Optional<Venta> buscarPorId(Integer id) {
        return facturaRepo.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Venta> buscarTodas() {
        return mapper.toDomainList(facturaRepo.findAll());
    }

    @Override
    public List<Venta> buscarPorUsuario(Integer idUsuario) {
        return mapper.toDomainList(facturaRepo.findByUsuarioId(idUsuario));
    }

    @Override
    public List<Venta> buscarDeHoy() {
        return mapper.toDomainList(facturaRepo.findFacturasDeHoy(new Date()));
    }

    @Override
    public List<Venta> buscarEntreFechas(Date inicio, Date fin) {
        return mapper.toDomainList(facturaRepo.findByFechaBetween(inicio, fin));
    }

    @Override
    public BigDecimal sumarTotalDelDia(Date fecha) {
        BigDecimal total = facturaRepo.sumTotalVentasByFecha(fecha);
        return total != null ? total : BigDecimal.ZERO;
    }
}
