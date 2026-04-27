package com.sistemaventas.backend.facade;

import com.sistemaventas.backend.domain.ports.in.ProcesarVentaUseCase;
import com.sistemaventas.backend.dto.request.VentaRequest;
import com.sistemaventas.backend.dto.response.VentaResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * PATRÓN FACADE — mantiene la interfaz pública original hacia los controladores.
 * Ahora delega toda la lógica al Use Case ProcesarVentaUseCase,
 * en lugar de orquestar directamente los servicios internos.
 *
 * Beneficio: el controlador no necesita cambiar su contrato con el Facade,
 * y el Facade ya no tiene lógica de negocio propia.
 */
@Service
public class VentasFacade {

    private static final Logger log = LoggerFactory.getLogger(VentasFacade.class);

    private final ProcesarVentaUseCase procesarVentaUseCase;

    public VentasFacade(ProcesarVentaUseCase procesarVentaUseCase) {
        this.procesarVentaUseCase = procesarVentaUseCase;
    }

    /**
     * Delega el procesamiento de una venta completa al Use Case correspondiente.
     * Toda la lógica transaccional y de dominio vive en ProcesarVentaUseCaseImpl.
     */
    public VentaResponse procesarVenta(VentaRequest ventaRequest) {
        log.info("VentasFacade: delegando procesarVenta al UseCase");
        return procesarVentaUseCase.procesarVenta(ventaRequest);
    }

    /**
     * Delega la validación de una venta sin procesarla.
     */
    public void validarVentaCompleta(VentaRequest ventaRequest) {
        log.info("VentasFacade: delegando validarVenta al UseCase");
        procesarVentaUseCase.validarVenta(ventaRequest);
    }
}