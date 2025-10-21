package com.pagamentos.nimble.nimble_pagamento.usuario.infra;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.pagamentos.nimble.nimble_pagamento.usuario.domain.service.SenhaHashService;

@Service
public class BCryptSenhaHashingService implements SenhaHashService {

    private final PasswordEncoder passwordEncoder;

    public BCryptSenhaHashingService() {
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    public String gerarHash(String senha) {
        return passwordEncoder.encode(senha);
    }

    @Override
    public boolean verificarSenha(String senha, String hash) {
        return passwordEncoder.matches(senha, hash);
    }

}
