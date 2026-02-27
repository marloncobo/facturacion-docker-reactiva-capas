package com.facturacion.facturacion.dto;

import com.facturacion.facturacion.model.Role;
import lombok.Data;

@Data
public class RegisterRequest {
    public String username;
    public String password;
    public Role role;
}
