package com.pagamentos.nimble.nimble_pagamento.usuario.domain.service;

public interface SenhaHashService {

    String gerarHash(String senha);
    boolean verificarSenha(String senha, String hash);

}
