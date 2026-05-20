package com.sistemaventas.backend.infrastructure.web.controller;

import com.sistemaventas.backend.infrastructure.web.dto.factus.FactusEmitirRequest;
import com.sistemaventas.backend.infrastructure.web.dto.factus.FactusEmitirResponse;
import com.sistemaventas.backend.infrastructure.factus.FactusService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/factus")
@CrossOrigin(origins = "http://localhost:4200")
public class FactusController {

    private final FactusService factusService;

    public FactusController(FactusService factusService) {
        this.factusService = factusService;
    }

    @PostMapping("/emitir")
    public ResponseEntity<FactusEmitirResponse> emitir(@Valid @RequestBody FactusEmitirRequest request) {
        FactusEmitirResponse response = factusService.emitir(request);
        return response.exito()
                ? ResponseEntity.ok(response)
                : ResponseEntity.badRequest().body(response);
    }
}
