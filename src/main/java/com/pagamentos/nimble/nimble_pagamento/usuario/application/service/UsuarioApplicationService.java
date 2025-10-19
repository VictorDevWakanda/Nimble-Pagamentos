package com.pagamentos.nimble.nimble_pagamento.usuario.application.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.pagamentos.nimble.nimble_pagamento.usuario.application.api.UsuarioAlteracaoRequest;
import com.pagamentos.nimble.nimble_pagamento.usuario.application.api.UsuarioDetalhadoResponse;
import com.pagamentos.nimble.nimble_pagamento.usuario.application.api.UsuarioRequest;
import com.pagamentos.nimble.nimble_pagamento.usuario.application.api.UsuarioResponse;
import com.pagamentos.nimble.nimble_pagamento.usuario.application.repository.UsuarioRepository;
import com.pagamentos.nimble.nimble_pagamento.usuario.domain.Usuario;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
@RequiredArgsConstructor
public class UsuarioApplicationService implements UsuarioService {

    // private final PasswordEncoder passwordEncoder;
    private final UsuarioRepository usuarioRepository;

    @Override
    public UsuarioResponse criaUsuario(UsuarioRequest usuarioRequest) {
        log.info("[Inicia] UsuarioApplicationService - criaUsuario");
        Usuario novoUsuario = new Usuario(usuarioRequest);
        usuarioRepository.salvaUsuario(novoUsuario);
        log.info("[Finaliza] UsuarioApplicationService - criaUsuario");
        return new UsuarioResponse(novoUsuario.getIdUsuario());
    }

    @Override
    public UsuarioDetalhadoResponse buscaUsuarioAtravesId(UUID idUsuario) {
        log.info("[Inicia] UsuarioApplicationService - buscaUsuarioAtravesId");
        Usuario usuario = usuarioRepository.buscaUsuarioAtravesId(idUsuario);
        log.info("[Finaliza] UsuarioApplicationService - buscaUsuarioAtravesId");
        return new UsuarioDetalhadoResponse(usuario);
    }

    @Override
    public void deletaUsuarioAtravesId(UUID idUsuario) {
        log.info("[Inicia] UsuarioApplicationService - deletaUsuarioAtravesId");
        Usuario usuario = usuarioRepository.buscaUsuarioAtravesId(idUsuario);
        usuarioRepository.deletaUsuario(usuario);
        log.info("[Finaliza] UsuarioApplicationService - deletaUsuarioAtravesId");
    }

    @Override
    public void patchAlteraUsuario(UUID idUsuario, UsuarioAlteracaoRequest usuarioAlteracaoRequest) {
        log.info("[Inicia] UsuarioApplicationService - patchAlteraUsuario");
        Usuario usuario = usuarioRepository.buscaUsuarioAtravesId(idUsuario);
        usuario.altera(usuarioAlteracaoRequest);
        usuarioRepository.salvaUsuario(usuario);
        log.info("[Finaliza] UsuarioApplicationService - patchAlteraUsuario");
    }

}
