package com.facturacion.facturacion.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.facturacion.facturacion.model.Client;
import com.facturacion.facturacion.service.ClientService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/clients")
@PreAuthorize("hasRole('ADMIN')")
public class ClientController {

    private final ClientService service;

    public ClientController(ClientService service) {
        this.service = service;
    }

    @GetMapping
    public Flux<Client> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Mono<Client> getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public Mono<Client> create(@RequestBody Client client) {
        return service.save(client);
    }

    @PutMapping("/{id}")
    public Mono<Client> update(@PathVariable Long id, @RequestBody Client client) {
        return service.update(id, client);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> delete(@PathVariable Long id) {
        return service.delete(id);
    }
}
