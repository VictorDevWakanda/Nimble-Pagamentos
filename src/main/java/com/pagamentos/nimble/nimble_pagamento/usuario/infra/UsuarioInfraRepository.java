package com.pagamentos.nimble.nimble_pagamento.usuario.infra;

import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import com.pagamentos.nimble.nimble_pagamento.handler.APIException;
import com.pagamentos.nimble.nimble_pagamento.usuario.application.repository.UsuarioRepository;
import com.pagamentos.nimble.nimble_pagamento.usuario.domain.Usuario;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Repository
@Log4j2
@RequiredArgsConstructor
public class UsuarioInfraRepository implements UsuarioRepository {

    private final UsuarioSpringDataJPARepository usuarioSpringDataJPARepository;

    @Override
    public Usuario salvaUsuario(Usuario novoUsuario) {
        log.info("[Inicia] UsuarioInfraRepository - salva");
        try {
            usuarioSpringDataJPARepository.save(novoUsuario);

        } catch (DataIntegrityViolationException e) {
            throw APIException.build(HttpStatus.CONFLICT, "Usuario já existe!");
        }
        log.info("[Finaliza] UsuarioInfraRepository - salva");
        return novoUsuario;
    }

    @Override
    public Usuario buscaUsuarioAtravesId(UUID idUsuario) {
        log.info("[Inicia] UsuarioInfraRepository - buscaUsuarioAtravesId");
        Usuario usuario = usuarioSpringDataJPARepository
                .findById(idUsuario)
                .orElseThrow(() -> APIException.build(HttpStatus.NOT_FOUND, "Usuario não encontrado"));
        log.info("[Finaliza] UsuarioInfraRepository - buscaUsuarioAtravesId");
        return usuario;
    }

    @Override
    public void deletaUsuario(Usuario usuario) {
        log.info("[Inicia] UsuarioInfraRepository - deletaUsuario");
        usuarioSpringDataJPARepository.delete(usuario);
        log.info("[Finaliza] UsuarioInfraRepository - deletaUsuario");
    }

    @Override
    public Usuario buscaUsuarioPorCpf(String cpfUsuario) {
       log.info("[Inicia] UsuarioInfraRepository - buscaUsuarioPorCpf");
        Usuario usuario = usuarioSpringDataJPARepository
                .findByCpf(cpfUsuario)
                .orElseThrow(() -> APIException.build(HttpStatus.NOT_FOUND, "Usuario não encontrado"));
        log.info("[Finaliza] UsuarioInfraRepository - buscaUsuarioPorCpf");
        return usuario;
    }

}
