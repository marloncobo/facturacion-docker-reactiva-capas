package com.facturacion.facturacion.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("clients")
public class Client {

    @Id
    private Long id;

    private String name;

    private String email;
}
