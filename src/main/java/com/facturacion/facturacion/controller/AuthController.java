package com.facturacion.facturacion.controller;

import org.springframework.web.bind.annotation.*;
import com.facturacion.facturacion.dto.*;
import com.facturacion.facturacion.service.AuthService;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/register")
    public Mono<Void> register(@RequestBody RegisterRequest req) {
        return service.register(req);
    }

    @PostMapping("/login")
    public Mono<AuthResponse> login(@RequestBody AuthRequest req) {
        return service.login(req);
    }
}
