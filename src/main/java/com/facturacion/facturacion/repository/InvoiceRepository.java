package com.facturacion.facturacion.repository;

import com.facturacion.facturacion.model.Invoice;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface InvoiceRepository extends R2dbcRepository<Invoice, Long> {
}
