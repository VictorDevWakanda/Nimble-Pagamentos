package com.pagamentos.nimble.nimble_pagamento.usuario.application.api;

import java.util.UUID;

import com.pagamentos.nimble.nimble_pagamento.usuario.domain.Usuario;

import lombok.Value;

@Value
public class UsuarioDetalhadoResponse {
    private UUID idUsuario;
    private String nome;
    private String email;
    private String cpf;

    public UsuarioDetalhadoResponse(Usuario usuario) {
        this.idUsuario = usuario.getIdUsuario();
        this.cpf = usuario.getCpf();
        this.nome = usuario.getNome();
        this.email = usuario.getEmail();
    }
}
