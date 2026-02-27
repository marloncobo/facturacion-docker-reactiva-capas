package com.facturacion.facturacion.repository;

import com.facturacion.facturacion.model.Client;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface ClientRepository extends R2dbcRepository<Client, Long> {
}
