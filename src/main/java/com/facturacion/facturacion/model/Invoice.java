package com.facturacion.facturacion.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("invoices")
public class Invoice {

    @Id
    private Long id;

    private String description;

    private Double total;

    @Column("client_id")
    private Long clientId;

    @Transient
    private Client client;

    // Lombok genera los getters y setters estándar.
    // Mantenemos este setter manual solo porque tiene lógica adicional
    // para sincronizar el clientId.
    public void setClient(Client client) {
        this.client = client;
        if (client != null) {
            this.clientId = client.getId();
        }
    }
}
