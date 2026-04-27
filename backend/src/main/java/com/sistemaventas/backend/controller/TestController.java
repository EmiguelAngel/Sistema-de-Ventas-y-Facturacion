package com.sistemaventas.backend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sistemaventas.backend.dto.request.VentaRequest;

@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "http://localhost:4200")
public class TestController {

    private static final Logger log = LoggerFactory.getLogger(TestController.class);

    @GetMapping("/ping")
    public String ping() {
        return "Backend funcionando correctamente - " + java.time.LocalDateTime.now();
    }

    @PostMapping("/echo")
    public String echo(@RequestBody String body) {
        log.debug("Echo recibido: {}", body);
        return "Echo: " + body;
    }

    @PostMapping("/venta-test")
    public String ventaTest(@RequestBody String body) {
        log.debug("Datos de venta recibidos: {}", body);
        return "Peticion de venta recibida correctamente. Datos: " + body;
    }

    @PostMapping("/venta-objeto")
    public String ventaObjeto(@RequestBody VentaRequest ventaRequest) {
        log.debug("VentaRequest parseado: {}", ventaRequest);
        return "VentaRequest parseado correctamente: " + ventaRequest;
    }
}
