package com.pagamentos.nimble.nimble_pagamento.usuario.application.api;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.pagamentos.nimble.nimble_pagamento.docs.swagger.UsuarioAPIDocs;
import com.pagamentos.nimble.nimble_pagamento.usuario.application.service.UsuarioService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping("/usuario")
@RequiredArgsConstructor
@Log4j2
public class UsuarioAPI {
        private final UsuarioService usuarioService;

        @UsuarioAPIDocs.CadastroUsuario
        @PostMapping()
        @ResponseStatus(HttpStatus.CREATED)
        public UsuarioResponse postUsuario(@RequestBody @Valid UsuarioRequest usuarioRequest) {
                log.info("[Inicia] UsuarioAPI - postUsuario");
                UsuarioResponse usuarioCriado = usuarioService.criaUsuario(usuarioRequest);
                log.info("[Finaliza] UsuarioAPI - postUsuario");
                return usuarioCriado;
        }

        @UsuarioAPIDocs.ConsultaUsuarioPorId
        @GetMapping(value = "/{idUsuario}")
        @ResponseStatus(HttpStatus.OK)
        public UsuarioDetalhadoResponse getUsuarioAtravesId(@PathVariable UUID idUsuario) {
                log.info("[Inicia] UsuarioAPI - getUsuarioAtravesId");
                UsuarioDetalhadoResponse usuarioDetalhado = usuarioService.buscaUsuarioAtravesId(idUsuario);
                log.info("[Finaliza] UsuarioAPI - getUsuarioAtravesId");
                return usuarioDetalhado;
        }

        @UsuarioAPIDocs.DeletaUsuario
        @DeleteMapping(value = "/{idUsuario}")
        @ResponseStatus(HttpStatus.NO_CONTENT)
        public void deletaUsuarioAtravesId(@PathVariable UUID idUsuario) {
                log.info("[Inicia] UsuarioAPI - deletaUsuarioAtravesId");
                usuarioService.deletaUsuarioAtravesId(idUsuario);
                log.info("[Finaliza] UsuarioAPI - deletaUsuarioAtravesId");
        }

        @UsuarioAPIDocs.AlteraUsuario
        @PatchMapping(value = "/{idUsuario}")
        @ResponseStatus(HttpStatus.NO_CONTENT)
        public void patchAlteraUsuario(@PathVariable UUID idUsuario,
                        @Valid @RequestBody UsuarioAlteracaoRequest usuarioAlteracaoRequest) {
                log.info("[Inicia] UsuarioAPI - patchAlteraUsuario");
                usuarioService.patchAlteraUsuario(idUsuario, usuarioAlteracaoRequest);
                log.info("[Finaliza] UsuarioAPI - patchAlteraUsuario");
        }
}
