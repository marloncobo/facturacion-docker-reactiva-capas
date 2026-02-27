package com.facturacion.facturacion.service;

import org.springframework.stereotype.Service;
import com.facturacion.facturacion.model.Client;
import com.facturacion.facturacion.repository.ClientRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ClientService {

    private final ClientRepository repo;

    public ClientService(ClientRepository repo) {
        this.repo = repo;
    }

    public Flux<Client> findAll() {
        return repo.findAll();
    }

    public Mono<Client> findById(Long id) {
        return repo.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Client not found")));
    }

    public Mono<Client> save(Client client) {
        return repo.save(client);
    }

    public Mono<Client> update(Long id, Client client) {
        return repo.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Client not found")))
                .flatMap(existing -> {
                    existing.setName(client.getName());
                    existing.setEmail(client.getEmail());
                    return repo.save(existing);
                });
    }

    public Mono<Void> delete(Long id) {
        return repo.deleteById(id);
    }
}
