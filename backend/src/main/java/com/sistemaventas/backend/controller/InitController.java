package com.sistemaventas.backend.controller;

import java.math.BigDecimal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sistemaventas.backend.dto.request.ProductoRequest;
import com.sistemaventas.backend.domain.model.UsuarioDomain;
import com.sistemaventas.backend.domain.ports.in.ConsultarProductoUseCase;
import com.sistemaventas.backend.domain.ports.in.CrearProductoUseCase;
import com.sistemaventas.backend.domain.ports.in.GestionarUsuarioUseCase;

@RestController
@RequestMapping("/api/init")
@CrossOrigin(origins = "http://localhost:4200")
public class InitController {

    private final GestionarUsuarioUseCase gestionarUsuarioUseCase;
    private final CrearProductoUseCase crearProductoUseCase;
    private final ConsultarProductoUseCase consultarProductoUseCase;

    public InitController(GestionarUsuarioUseCase gestionarUsuarioUseCase,
                          CrearProductoUseCase crearProductoUseCase,
                          ConsultarProductoUseCase consultarProductoUseCase) {
        this.gestionarUsuarioUseCase = gestionarUsuarioUseCase;
        this.crearProductoUseCase = crearProductoUseCase;
        this.consultarProductoUseCase = consultarProductoUseCase;
    }

    @PostMapping("/datos-prueba")
    public ResponseEntity<String> crearDatosDePrueba() {
        try {
            // Crear usuario de prueba si no existe
            if (gestionarUsuarioUseCase.buscarPorId(1).isEmpty()) {
                UsuarioDomain usuario = new UsuarioDomain();
                usuario.setId(1);
                usuario.setNombre("Juan Pérez");
                usuario.setCorreo("juan@test.com");
                usuario.setContrasena("123456");
                usuario.setIdRol(1); // Administrador
                gestionarUsuarioUseCase.crear(usuario);
            }

            // Crear productos de prueba si no existen
            if (consultarProductoUseCase.buscarPorId(1).isEmpty()) {
                ProductoRequest producto1 = new ProductoRequest();
                producto1.setIdProducto(1);
                producto1.setDescripcion("Coca Cola 600ml");
                producto1.setPrecioUnitario(new BigDecimal("2.50"));
                producto1.setCantidadDisponible(100);
                producto1.setCategoria("Bebidas");
                crearProductoUseCase.crearProducto(producto1);
            }

            if (consultarProductoUseCase.buscarPorId(2).isEmpty()) {
                ProductoRequest producto2 = new ProductoRequest();
                producto2.setIdProducto(2);
                producto2.setDescripcion("Pan Integral");
                producto2.setPrecioUnitario(new BigDecimal("1.20"));
                producto2.setCantidadDisponible(50);
                producto2.setCategoria("Panadería");
                crearProductoUseCase.crearProducto(producto2);
            }

            return ResponseEntity.ok("✅ Datos de prueba creados exitosamente: Usuario ID 1 y Productos ID 1,2");

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("❌ Error creando datos: " + e.getMessage());
        }
    }
}