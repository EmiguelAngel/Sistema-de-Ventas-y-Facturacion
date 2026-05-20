package com.sistemaventas.backend.application.service;

import com.sistemaventas.backend.domain.ports.in.ProcesarVentaUseCase;
import com.sistemaventas.backend.infrastructure.web.dto.request.VentaRequest;
import com.sistemaventas.backend.infrastructure.web.dto.response.VentaResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class VentasFacade {

    private static final Logger log = LoggerFactory.getLogger(VentasFacade.class);

    private final ProcesarVentaUseCase procesarVentaUseCase;

    public VentasFacade(ProcesarVentaUseCase procesarVentaUseCase) {
        this.procesarVentaUseCase = procesarVentaUseCase;
    }

    public VentaResponse procesarVenta(VentaRequest ventaRequest) {
        log.info("VentasFacade: delegando procesarVenta al UseCase");
        return procesarVentaUseCase.procesarVenta(ventaRequest);
    }

    public void validarVentaCompleta(VentaRequest ventaRequest) {
        log.info("VentasFacade: delegando validarVenta al UseCase");
        procesarVentaUseCase.validarVenta(ventaRequest);
    }
}
