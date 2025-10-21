package com.pagamentos.nimble.nimble_pagamento.usuario.application.api;

import jakarta.validation.constraints.NotBlank;
import lombok.Value;

@Value
public class UsuarioRequest {
    @NotBlank
    private String nome;
    @NotBlank
    private String cpf;
    @NotBlank
    private String email;
    @NotBlank
    private String senha;
}
