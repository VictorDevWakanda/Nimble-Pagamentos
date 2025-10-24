package com.pagamentos.nimble.nimble_pagamento.cobranca.application.api;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.pagamentos.nimble.nimble_pagamento.cobranca.application.service.CobrancaService;
import com.pagamentos.nimble.nimble_pagamento.cobranca.domain.StatusCobranca;
import com.pagamentos.nimble.nimble_pagamento.docs.swagger.CobrancaAPIDocs;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping("/cobrancas")
@RequiredArgsConstructor
@Log4j2
@Tag(name = "CobrancaAPI", description = "Operações relacionadas à cobrança.")
public class CobrancaAPI {

    private final CobrancaService cobrancaService;

    @CobrancaAPIDocs.CadastraCobranca
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public CobrancaResponse postCriaCobranca(@RequestParam UUID IdUsuarioOriginador,
            @RequestBody @Valid CobrancaRequest cobrancaRequest) {
        log.info("[Inicia] CobrancaAPI - criaCobranca");
        CobrancaResponse cobrancaCriada = cobrancaService.criaCobranca(IdUsuarioOriginador, cobrancaRequest);
        log.debug("[Finaliza] CobrancaAPI - criaCobranca");
        return cobrancaCriada;
    }

    @CobrancaAPIDocs.ConsultaCobrancaPorId
    @GetMapping("/consulta")
    @ResponseStatus(HttpStatus.OK)
    public CobrancaDetalhadaResponse getBuscaCobrancaPorId(@RequestParam UUID idCobranca,
            @RequestParam UUID idUsuarioSolicitante) {
        log.info("[Inicia] CobrancaAPI - buscaCobrancaPorId");
        CobrancaDetalhadaResponse cobranca = cobrancaService.buscaCobrancaPorId(idCobranca, idUsuarioSolicitante);
        log.debug("[Finaliza] CobrancaAPI - buscaCobrancaPorId");
        return cobranca;
    }

    @CobrancaAPIDocs.ListaCobrancaDoOriginador
    @GetMapping("/originador")
    @ResponseStatus(HttpStatus.OK)
    public PageResponse<CobrancaDestinatarioListResponse> getListaCobrancasCriadas(
            @RequestParam UUID IdusuarioOriginador, @PageableDefault(page = 0, size = 10) Pageable pageable,
            @RequestParam(required = false) StatusCobranca status) {
        log.info("[Inicia] CobrancaAPI - listaCobrancasCriadas");
        Page<CobrancaDestinatarioListResponse> cobrancas = cobrancaService
                .listaCobrancasCriadas(IdusuarioOriginador, pageable, status);
        log.debug("[Finaliza] CobrancaAPI - listaCobrancasCriadas");
        return new PageResponse<>(cobrancas);
    }

    @CobrancaAPIDocs.ListaCobrancaDoDestinatario
    @GetMapping("/destinatario")
    @ResponseStatus(HttpStatus.OK)
    public PageResponse<CobrancaOriginadorListResponse> getListaCobrancasRecebidas(
            @RequestParam UUID idUsuarioSolicitante, @PageableDefault(page = 0, size = 10) Pageable pageable,
            @RequestParam(required = false) StatusCobranca status) {
        log.info("[Inicia] CobrancaAPI - listaCobrancasRecebidas");
        Page<CobrancaOriginadorListResponse> cobrancas = cobrancaService
                .listaCobrancasRecebidas(idUsuarioSolicitante, pageable, status);
        log.debug("[Finaliza] CobrancaAPI - listaCobrancasRecebidas");
        return new PageResponse<>(cobrancas);
    }

    @CobrancaAPIDocs.EfetuaPagamento
    @PostMapping("/pagamento/{idCobranca}")
    @ResponseStatus(HttpStatus.OK)
    public void postEfetuaPagamento(@PathVariable UUID idCobranca, @RequestParam UUID idUsuarioPagador) {
        log.info("[Inicia] CobrancaAPI - efetuaPagamento");
        cobrancaService.efetuaPagamento(idCobranca, idUsuarioPagador);
        log.debug("[Finaliza] CobrancaAPI - efetuaPagamento");
    }

    @CobrancaAPIDocs.EfetuaPagamentoComCartao
    @PostMapping("/pagamento-cartao/{idCobranca}")
    @ResponseStatus(HttpStatus.OK)
    public void postEfetuaPagamentoComCartao(
            @PathVariable UUID idCobranca,
            @RequestParam UUID idUsuarioPagador,
            @RequestBody @Valid PagamentoCartaoRequest pagamentoCartaoRequest) {
        log.info("[Inicia] CobrancaAPI - efetuaPagamentoComCartao");
        cobrancaService.efetuaPagamentoComCartao(idCobranca, idUsuarioPagador, pagamentoCartaoRequest);
        log.debug("[Finaliza] CobrancaAPI - efetuaPagamentoComCartao");
    }

    @CobrancaAPIDocs.CancelaCobranca
    @PostMapping("/{idCobranca}/cancelamento")
    @ResponseStatus(HttpStatus.OK)
    public void postCancelaCobranca(
            @PathVariable UUID idCobranca,
            @RequestParam UUID idUsuarioSolicitante) {
        log.info("[Inicia] CobrancaAPI - cancelaCobranca");
        cobrancaService.cancelaCobranca(idCobranca, idUsuarioSolicitante);
        log.debug("[Finaliza] CobrancaAPI - cancelaCobranca");
    }

    @CobrancaAPIDocs.RealizaDeposito
    @PostMapping("/deposito")
    @ResponseStatus(HttpStatus.OK)
    public void postRealizaDeposito(
            @RequestParam UUID idUsuario,
            @RequestBody @Valid DepositoRequest depositoRequest) {
        log.info("[Inicia] CobrancaAPI - realizaDeposito");
        cobrancaService.realizaDeposito(idUsuario, depositoRequest);
        log.debug("[Finaliza] CobrancaAPI - realizaDeposito");
    }

}
