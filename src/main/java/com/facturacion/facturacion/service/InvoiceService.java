package com.facturacion.facturacion.service;

import org.springframework.stereotype.Service;
import com.facturacion.facturacion.dto.InvoiceDTO;
import com.facturacion.facturacion.model.Invoice;
import com.facturacion.facturacion.repository.ClientRepository;
import com.facturacion.facturacion.repository.InvoiceRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepo;
    private final ClientRepository clientRepo;

    public InvoiceService(InvoiceRepository invoiceRepo, ClientRepository clientRepo) {
        this.invoiceRepo = invoiceRepo;
        this.clientRepo = clientRepo;
    }

    public Flux<Invoice> findAll() {
        return invoiceRepo.findAll()
                .flatMap(invoice -> {
                    if (invoice.getClientId() != null) {
                        return clientRepo.findById(invoice.getClientId())
                                .map(client -> {
                                    invoice.setClient(client);
                                    return invoice;
                                })
                                .defaultIfEmpty(invoice);
                    } else {
                        return Mono.just(invoice);
                    }
                });
    }

    public Mono<Invoice> findById(Long id) {
        return invoiceRepo.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Invoice not found")))
                .flatMap(invoice -> {
                    if (invoice.getClientId() != null) {
                        return clientRepo.findById(invoice.getClientId())
                                .map(client -> {
                                    invoice.setClient(client);
                                    return invoice;
                                })
                                .defaultIfEmpty(invoice);
                    } else {
                        return Mono.just(invoice);
                    }
                });
    }

    public Mono<Invoice> save(InvoiceDTO dto) {
        return clientRepo.findById(dto.clientId)
                .switchIfEmpty(Mono.error(new RuntimeException("Client not found")))
                .flatMap(client -> {
                    Invoice invoice = new Invoice();
                    invoice.setDescription(dto.description);
                    invoice.setTotal(dto.total);
                    invoice.setClientId(client.getId());
                    invoice.setClient(client);
                    return invoiceRepo.save(invoice);
                });
    }

    public Mono<Invoice> update(Long id, InvoiceDTO dto) {
        return invoiceRepo.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Invoice not found")))
                .flatMap(invoice -> clientRepo.findById(dto.clientId)
                        .switchIfEmpty(Mono.error(new RuntimeException("Client not found")))
                        .flatMap(client -> {
                            invoice.setDescription(dto.description);
                            invoice.setTotal(dto.total);
                            invoice.setClientId(client.getId());
                            invoice.setClient(client);
                            return invoiceRepo.save(invoice);
                        }));
    }

    public Mono<Void> delete(Long id) {
        return invoiceRepo.deleteById(id);
    }
}
