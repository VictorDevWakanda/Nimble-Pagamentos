package com.pagamentos.nimble.nimble_pagamento.usuario.application.api;

import lombok.Value;

@Value
public class UsuarioAlteracaoRequest {

    private String nome;

    private String email;

    private String senha;
}
