package com.facturacion.facturacion.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.facturacion.facturacion.dto.*;
import com.facturacion.facturacion.model.User;
import com.facturacion.facturacion.repository.UserRepository;
import com.facturacion.facturacion.security.JwtService;
import reactor.core.publisher.Mono;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.Duration;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository repo;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AuthService(UserRepository repo, JwtService jwtService) {
        this.repo = repo;
        this.jwtService = jwtService;
    }

    public Mono<Void> register(RegisterRequest req) {
        log.info("Intentando registrar usuario: {}", req.getUsername());
        return repo.findByUsername(req.getUsername())
                .flatMap(existing -> {
                    log.warn("El usuario {} ya existe", req.getUsername());
                    return Mono.error(new RuntimeException("Username already exists"));
                })
                .switchIfEmpty(Mono.defer(() -> {
                    log.info("Usuario {} no encontrado, procediendo a crear", req.getUsername());
                    User user = new User();
                    user.setUsername(req.getUsername());
                    user.setPassword(encoder.encode(req.getPassword()));
                    user.setRole(req.getRole());
                    return repo.save(user)
                            .doOnSuccess(u -> log.info("Usuario {} guardado con ID: {}", u.getUsername(), u.getId()))
                            .doOnError(e -> log.error("Error al guardar usuario {}: {}", req.getUsername(), e.getMessage()));
                }))
                .timeout(Duration.ofSeconds(10)) // Timeout de 10 segundos
                .then();
    }

    public Mono<AuthResponse> login(AuthRequest req) {
        return repo.findByUsername(req.getUsername())
                .switchIfEmpty(Mono.error(new RuntimeException("User not found")))
                .flatMap(user -> {
                    if (!encoder.matches(req.getPassword(), user.getPassword())) {
                        return Mono.error(new RuntimeException("Credenciales incorrectas"));
                    }
                    return Mono.just(new AuthResponse(jwtService.generateToken(user.getUsername())));
                });
    }
}
