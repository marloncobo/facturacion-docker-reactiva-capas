package com.facturacion.facturacion.controller;

import org.springframework.web.bind.annotation.*;
import com.facturacion.facturacion.dto.InvoiceDTO;
import com.facturacion.facturacion.model.Invoice;
import com.facturacion.facturacion.service.InvoiceService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/invoices")
public class InvoiceController {

    private final InvoiceService service;

    public InvoiceController(InvoiceService service) {
        this.service = service;
    }

    @GetMapping
    public Flux<Invoice> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Mono<Invoice> getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public Mono<Invoice> create(@RequestBody InvoiceDTO dto) {
        return service.save(dto);
    }

    @PutMapping("/{id}")
    public Mono<Invoice> update(@PathVariable Long id, @RequestBody InvoiceDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> delete(@PathVariable Long id) {
        return service.delete(id);
    }
}
