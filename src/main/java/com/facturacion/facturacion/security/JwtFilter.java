package com.facturacion.facturacion.security;

import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class JwtFilter implements WebFilter {

    private final JwtService jwtService;
    private final ReactiveUserDetailsService userDetailsService;

    public JwtFilter(JwtService jwtService, ReactiveUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // 1. Si es una ruta pública (auth), no filtramos
        if (path.startsWith("/auth/")) {
            return chain.filter(exchange);
        }

        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return chain.filter(exchange);
        }

        String token = authHeader.substring(7);
        
        try {
            String username = jwtService.extractUsername(token);

            if (username != null) {
                return userDetailsService.findByUsername(username)
                        .flatMap(userDetails -> {
                            if (jwtService.validateToken(token, userDetails.getUsername())) {
                                UsernamePasswordAuthenticationToken auth =
                                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                                
                                // CRUCIAL: contextWrite es lo que "loguea" al usuario en el contexto reactivo
                                return chain.filter(exchange)
                                        .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));
                            }
                            return chain.filter(exchange);
                        })
                        .switchIfEmpty(chain.filter(exchange)); // Si no encuentra el usuario, continúa sin loguear
            }
        } catch (Exception e) {
            System.out.println("Error validando JWT: " + e.getMessage());
        }

        return chain.filter(exchange);
    }
}