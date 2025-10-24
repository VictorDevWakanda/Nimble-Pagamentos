package com.pagamentos.nimble.nimble_pagamento.cobranca.application.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.pagamentos.nimble.nimble_pagamento.autorizador.gateway.AutorizadorPagamentos;
import com.pagamentos.nimble.nimble_pagamento.cobranca.application.api.CobrancaDestinatarioListResponse;
import com.pagamentos.nimble.nimble_pagamento.cobranca.application.api.CobrancaDetalhadaResponse;
import com.pagamentos.nimble.nimble_pagamento.cobranca.application.api.CobrancaOriginadorListResponse;
import com.pagamentos.nimble.nimble_pagamento.cobranca.application.api.CobrancaRequest;
import com.pagamentos.nimble.nimble_pagamento.cobranca.application.api.CobrancaResponse;
import com.pagamentos.nimble.nimble_pagamento.cobranca.application.api.DepositoRequest;
import com.pagamentos.nimble.nimble_pagamento.cobranca.application.api.PagamentoCartaoRequest;
import com.pagamentos.nimble.nimble_pagamento.cobranca.application.api.ValidadorRequest;
import com.pagamentos.nimble.nimble_pagamento.cobranca.application.repository.CobrancaRepository;
import com.pagamentos.nimble.nimble_pagamento.cobranca.domain.Cobranca;
import com.pagamentos.nimble.nimble_pagamento.cobranca.domain.StatusCobranca;
import com.pagamentos.nimble.nimble_pagamento.handler.APIException;
import com.pagamentos.nimble.nimble_pagamento.usuario.application.repository.UsuarioRepository;
import com.pagamentos.nimble.nimble_pagamento.usuario.domain.Usuario;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class CobrancaApplicationService implements CobrancaService {

    private final CobrancaRepository cobrancaRepository;
    private final UsuarioRepository usuarioRepository;
    private final AutorizadorPagamentos autorizadorPagamentos;

    @Override
    public CobrancaResponse criaCobranca(UUID idUsuarioOriginador, CobrancaRequest request) {
        log.info("[Inicia] CobrancaApplicationService - criaCobranca");
        Cobranca cobranca = new Cobranca(idUsuarioOriginador, request);
        if (null == usuarioRepository.buscaUsuarioAtravesId(idUsuarioOriginador)) {
            throw APIException.build(HttpStatus.BAD_REQUEST, "Usuário originador não encontrado.");
        }
        usuarioRepository.buscaUsuarioPorCpf(request.cpfDestinatario());
        cobrancaRepository.salvaCobranca(cobranca);
        log.debug("[Finaliza] CobrancaApplicationService - criaCobranca");
        return new CobrancaResponse(cobranca.getIdCobranca());
    }

    @Override
    public CobrancaDetalhadaResponse buscaCobrancaPorId(UUID idCobranca, UUID idUsuarioSolicitante) {
        log.info("[Inicia] CobrancaApplicationService - buscaCobrancaPorId");
        Cobranca cobranca = cobrancaRepository.buscaCobrancaPorId(idCobranca);
        Usuario usuario = usuarioRepository.buscaUsuarioAtravesId(idUsuarioSolicitante);
        boolean autorizado = cobranca.pertenceAoOriginador(usuario.getIdUsuario())
                || cobranca.ehDestinatario(usuario.getCpf());
        if (!autorizado) {
            throw APIException.build(HttpStatus.FORBIDDEN, "Usuário não autorizado a visualizar esta cobrança.");
        }
        log.debug("[Finaliza] CobrancaApplicationService - buscaCobrancaPorId");
        return new CobrancaDetalhadaResponse(cobranca);
    }

    @Override
    public Page<CobrancaDestinatarioListResponse> listaCobrancasCriadas(UUID IdusuarioOriginador,
            Pageable pageable, StatusCobranca status) {
        log.info("[Inicia] CobrancaApplicationService - listaCobrancasCriadas");
        Page<Cobranca> cobrancas = cobrancaRepository.buscaCobrancasCriadas(IdusuarioOriginador, pageable, status);
        log.debug("[Finaliza] CobrancaApplicationService - listaCobrancasCriadas");
        return CobrancaDestinatarioListResponse.converte(cobrancas);
    }

    @Override
    public Page<CobrancaOriginadorListResponse> listaCobrancasRecebidas(UUID idUsuarioSolicitante, Pageable pageable,
            StatusCobranca status) {
        log.info("[Inicia] CobrancaApplicationService - listaCobrancasRecebidas");
        Usuario usuario = usuarioRepository.buscaUsuarioAtravesId(idUsuarioSolicitante);
        Page<Cobranca> cobrancas = cobrancaRepository.buscaCobrancasRecebidas(usuario.getCpfLimpo(), pageable, status);
        List<CobrancaOriginadorListResponse> cobrancasList = cobrancas.stream()
                .map(c -> {
                    Usuario originador = usuarioRepository.buscaUsuarioAtravesId(c.getIdUsuarioOriginador());
                    return new CobrancaOriginadorListResponse(originador.getCpf(), c);
                })
                .collect(Collectors.toList());

        log.debug("[Finaliza] CobrancaApplicationService - listaCobrancasRecebidas");
        return new PageImpl<>(cobrancasList, cobrancas.getPageable(), cobrancas.getTotalElements());
    }

    @Override
    public void efetuaPagamento(UUID idCobranca, UUID idUsuarioPagador) {
        log.info("[Inicia] CobrancaApplicationService - efetuaPagamento");
        Cobranca cobranca = cobrancaRepository.buscaCobrancaPorId(idCobranca);
        Usuario pagador = usuarioRepository.buscaUsuarioAtravesId(idUsuarioPagador);
        Usuario originador = usuarioRepository.buscaUsuarioAtravesId(cobranca.getIdUsuarioOriginador());
        ValidadorRequest validadorRequest = ValidadorRequest.paraPagamentoSaldo(cobranca.getIdCobranca(),
                pagador.getCpfLimpo(),
                cobranca.getValorCobranca());

        boolean autorizado = autorizadorPagamentos.validarAutorizacao(validadorRequest);
        if (!autorizado) {
            throw APIException.build(HttpStatus.BAD_REQUEST, "Pagamento não autorizado");
        }
        try {
            cobranca.efetuarPagamento(pagador);
        } catch (IllegalStateException e) {
            throw APIException.build(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        originador.creditar(cobranca.getValorCobranca());
        usuarioRepository.salvaUsuario(originador);
        usuarioRepository.salvaUsuario(pagador);
        cobrancaRepository.salvaCobranca(cobranca);
        log.info("[sucesso] Pagamento efetuado com sucesso para cobrança: {}", cobranca.getIdCobranca());
        log.debug("[Finaliza] CobrancaApplicationService - efetuaPagamento");

    }

    @Override
    public void efetuaPagamentoComCartao(UUID idCobranca, UUID idUsuarioPagador,
            PagamentoCartaoRequest pagamentoCartaoRequest) {
        log.info("[Inicia] CobrancaApplicationService - efetuaPagamentoComCartao");
        Cobranca cobranca = cobrancaRepository.buscaCobrancaPorId(idCobranca);
        Usuario pagadorCartao = usuarioRepository.buscaUsuarioAtravesId(idUsuarioPagador);
        Usuario originadorCartao = usuarioRepository.buscaUsuarioAtravesId(cobranca.getIdUsuarioOriginador());
        ValidadorRequest validadorRequest = ValidadorRequest.paraPagamentoCartao(
                cobranca.getIdCobranca(),
                pagadorCartao.getCpfLimpo(),
                cobranca.getValorCobranca(),
                pagamentoCartaoRequest.getNumeroCartao(),
                pagamentoCartaoRequest.getValidade(),
                pagamentoCartaoRequest.getCvv());
        boolean autorizado = autorizadorPagamentos.validarAutorizacao(validadorRequest);
        if (!autorizado) {
            throw APIException.build(HttpStatus.BAD_REQUEST, "Pagamento com cartão não autorizado");
        }
        try {
            cobranca.efetuarPagamentoComCartao();
        } catch (IllegalStateException e) {
            throw APIException.build(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        originadorCartao.creditar(cobranca.getValorCobranca());
        usuarioRepository.salvaUsuario(originadorCartao);
        cobrancaRepository.salvaCobranca(cobranca);
        log.info("[sucesso] Pagamento com cartão efetuado para cobrança: {}", cobranca.getIdCobranca());
        log.debug("[Finaliza] CobrancaApplicationService - efetuaPagamentoComCartao");
    }

    @Override
    public void cancelaCobranca(UUID idCobranca, UUID idUsuarioSolicitante) {
        log.info("[Inicia] CobrancaApplicationService - cancelaCobranca");

        Cobranca cobranca = cobrancaRepository.buscaCobrancaPorId(idCobranca);
        Usuario originador = usuarioRepository.buscaUsuarioAtravesId(idUsuarioSolicitante);

        validarPermissaoDeCancelamento(cobranca, originador);
        validarEstadoAtual(cobranca);

        if (cobranca.estaPendente()) {
            cancelarCobrancaPendente(cobranca);
        } else {
            cancelarCobrancaPaga(cobranca, originador);
        }

        cobrancaRepository.salvaCobranca(cobranca);
        log.info("[sucesso] Cobrança cancelada: {}", cobranca.getIdCobranca());
        log.debug("[Finaliza] CobrancaApplicationService - cancelaCobranca");
    }

    private void validarPermissaoDeCancelamento(Cobranca cobranca, Usuario originador) {
        if (!cobranca.pertenceAoOriginador(originador.getIdUsuario())) {
            throw APIException.build(HttpStatus.FORBIDDEN, "Somente o originador pode cancelar a cobrança.");
        }
    }

    private void validarEstadoAtual(Cobranca cobranca) {
        if (cobranca.estaCancelada()) {
            throw APIException.build(HttpStatus.BAD_REQUEST, "Cobrança já está cancelada.");
        }
    }

    private void cancelarCobrancaPendente(Cobranca cobranca) {
        cobranca.marcarComoCancelada();
    }

    private void cancelarCobrancaPaga(Cobranca cobranca, Usuario originador) {
        if (cobranca.foiPagaComSaldo()) {
            estornarPagamentoPorSaldo(cobranca, originador);
        } else if (cobranca.foiPagaComCartao()) {
            validarCancelamentoComCartao(cobranca, originador);
        }
        cobranca.cancelarComEstorno();
    }

    private void estornarPagamentoPorSaldo(Cobranca cobranca, Usuario originador) {
        Usuario pagador = usuarioRepository.buscaUsuarioPorCpf(cobranca.getCpfDestinatario());
        originador.debitar(cobranca.getValorCobranca());
        pagador.creditar(cobranca.getValorCobranca());
        usuarioRepository.salvaUsuario(originador);
        usuarioRepository.salvaUsuario(pagador);
    }

    private void validarCancelamentoComCartao(Cobranca cobranca, Usuario originador) {
        ValidadorRequest validadorRequest = ValidadorRequest.paraCancelamento(
                cobranca.getIdCobranca(),
                originador.getCpfLimpo(),
                cobranca.getValorCobranca());

        boolean autorizado = autorizadorPagamentos.validarCancelamento(validadorRequest);
        if (!autorizado) {
            throw APIException.build(HttpStatus.BAD_REQUEST, "Cancelamento não autorizado pelo autorizador externo.");
        }
    }

    @Override
    public void realizaDeposito(UUID idUsuario, DepositoRequest depositoRequest) {
        log.info("[Inicia] CobrancaApplicationService - realizaDeposito");

        Usuario usuario = usuarioRepository.buscaUsuarioAtravesId(idUsuario);

        ValidadorRequest validadorRequest = ValidadorRequest.paraDeposito(
                usuario.getCpfLimpo(),
                depositoRequest.getValor());

        boolean autorizado = autorizadorPagamentos.validarAutorizacao(validadorRequest);
        if (!autorizado) {
            throw APIException.build(HttpStatus.BAD_REQUEST, "Depósito não autorizado pelo autorizador externo.");
        }

        usuario.creditar(depositoRequest.getValor());
        usuarioRepository.salvaUsuario(usuario);

        log.info("[sucesso] Depósito realizado para usuário: {}", usuario.getIdUsuario());
        log.debug("[Finaliza] CobrancaApplicationService - realizaDeposito");
    }

}
