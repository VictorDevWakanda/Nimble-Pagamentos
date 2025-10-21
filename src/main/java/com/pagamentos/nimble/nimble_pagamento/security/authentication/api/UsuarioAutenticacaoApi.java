package com.pagamentos.nimble.nimble_pagamento.security.authentication.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.pagamentos.nimble.nimble_pagamento.security.authentication.service.UsuarioLoginService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/usuario/autenticacao")
@Tag(name = "UsuarioAuthAPI", description = "Autenticação do usuário.")

public class UsuarioAutenticacaoApi {
    private final UsuarioLoginService usuarioLoginService;

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public AutenticacaoResponseDto login(@RequestBody @Valid UsuarioLoginDto usuarioLoginDto) {
        log.info("[inicia] UsuarioAutenticacaoApi - login");
        AutenticacaoResponseDto response = usuarioLoginService.autenticarUsuario(usuarioLoginDto);
        log.info("[finaliza] UsuarioAutenticacaoApi - login");
        return response;
    }
}
