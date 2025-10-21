package com.pagamentos.nimble.nimble_pagamento.security.authentication.api;

import jakarta.validation.constraints.NotBlank;
import lombok.Value;

@Value
public class UsuarioLoginDto {
    @NotBlank
    private String cpfOuEmail;
    @NotBlank
    private String senha;
}
