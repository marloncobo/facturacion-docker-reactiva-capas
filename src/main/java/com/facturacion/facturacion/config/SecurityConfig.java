package com.facturacion.facturacion.config;

import com.facturacion.facturacion.security.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)

                // Stateless (equivalente a SessionCreationPolicy.STATELESS)
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())

                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((exchange, ex2) -> {
                            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                            return exchange.getResponse()
                                    .writeWith(Mono.just(
                                            exchange.getResponse()
                                                    .bufferFactory()
                                                    .wrap("No autenticado".getBytes())
                                    ));
                        })
                        .accessDeniedHandler((exchange, denied) -> {
                            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                            return exchange.getResponse()
                                    .writeWith(Mono.just(
                                            exchange.getResponse()
                                                    .bufferFactory()
                                                    .wrap("No autorizado".getBytes())
                                    ));
                        })
                )

                .authorizeExchange(auth -> auth
                        .pathMatchers("/auth/**").permitAll()

                        // CLIENTS
                        .pathMatchers("/clients/**").hasAnyRole("ADMIN", "USER")

                        // INVOICES - lectura
                        .pathMatchers(HttpMethod.GET, "/invoices/**")
                        .hasAnyRole("ADMIN", "USER")

                        // INVOICES - escritura
                        .pathMatchers(HttpMethod.POST, "/invoices/**")
                        .hasRole("ADMIN")

                        .pathMatchers(HttpMethod.PUT, "/invoices/**")
                        .hasRole("ADMIN")

                        .pathMatchers(HttpMethod.DELETE, "/invoices/**")
                        .hasRole("ADMIN")

                        .anyExchange().authenticated()
                )

                // JWT filter antes del AUTHENTICATION
                .addFilterBefore(jwtFilter, SecurityWebFiltersOrder.AUTHENTICATION)

                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)

                .build();
    }

    @Bean
    public ReactiveAuthenticationManager reactiveAuthenticationManager(
            ReactiveUserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {

        UserDetailsRepositoryReactiveAuthenticationManager authenticationManager =
                new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);

        authenticationManager.setPasswordEncoder(passwordEncoder);

        return authenticationManager;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


}
