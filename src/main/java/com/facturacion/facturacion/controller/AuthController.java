package com.facturacion.facturacion.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.facturacion.facturacion.dto.*;
import com.facturacion.facturacion.service.AuthService;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/register")
    public Mono<ResponseEntity<Map<String, String>>> register(@RequestBody RegisterRequest req) {
        return service.register(req)
                .then(Mono.just(ResponseEntity.status(HttpStatus.CREATED)
                        .body(Map.of("message", "Usuario registrado exitosamente"))));
    }

    @PostMapping("/login")
    public Mono<AuthResponse> login(@RequestBody AuthRequest req) {
        return service.login(req);
    }
}
