package com.pagamentos.nimble.nimble_pagamento.usuario.application.api;

import org.hibernate.validator.constraints.br.CPF;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Value;

@Value
public class UsuarioRequest {
    @NotBlank
    private String nome;
    @NotBlank
    @CPF
    private String cpf;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String senha;
}
