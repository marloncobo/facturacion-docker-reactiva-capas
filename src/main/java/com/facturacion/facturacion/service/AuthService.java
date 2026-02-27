package com.facturacion.facturacion.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.facturacion.facturacion.dto.*;
import com.facturacion.facturacion.model.User;
import com.facturacion.facturacion.repository.UserRepository;
import com.facturacion.facturacion.security.JwtService;
import reactor.core.publisher.Mono;

@Service
public class AuthService {

    private final UserRepository repo;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AuthService(UserRepository repo, JwtService jwtService) {
        this.repo = repo;
        this.jwtService = jwtService;
    }

    public Mono<Void> register(RegisterRequest req) {
        return repo.findByUsername(req.getUsername())
                .flatMap(existing -> Mono.error(new RuntimeException("Username already exists")))
                .switchIfEmpty(Mono.defer(() -> {
                    User user = new User();
                    user.setUsername(req.getUsername());
                    user.setPassword(encoder.encode(req.getPassword()));
                    user.setRole(req.getRole());
                    return repo.save(user);
                }))
                .then();
    }

    public Mono<AuthResponse> login(AuthRequest req) {
        return repo.findByUsername(req.username)
                .switchIfEmpty(Mono.error(new RuntimeException("User not found")))
                .flatMap(user -> {
                    if (!encoder.matches(req.password, user.getPassword())) {
                        return Mono.error(new RuntimeException("Credenciales incorrectas"));
                    }
                    return Mono.just(new AuthResponse(jwtService.generateToken(user.getUsername())));
                });
    }
}
