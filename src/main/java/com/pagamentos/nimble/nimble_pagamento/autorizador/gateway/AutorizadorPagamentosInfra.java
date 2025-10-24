package com.pagamentos.nimble.nimble_pagamento.autorizador.gateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.pagamentos.nimble.nimble_pagamento.cobranca.application.api.ValidadorRequest;
import com.pagamentos.nimble.nimble_pagamento.cobranca.application.api.ValidadorResponse;
import com.pagamentos.nimble.nimble_pagamento.handler.APIException;

import lombok.extern.log4j.Log4j2;

@Component
@Log4j2
public class AutorizadorPagamentosInfra implements AutorizadorPagamentos {

    private final WebClient webClient;

    @Value("${autorizador.pagamento.path}")
    private String pagamentoPath;

    @Value("${autorizador.cancelamento.path}")
    private String cancelamentoPath;

    public AutorizadorPagamentosInfra(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Override
    public boolean validarAutorizacao(ValidadorRequest validadorRequest) {
        log.info("[Inicia] AutorizadorPagamentosInfra - validarAutorizacao");

        try {
            ValidadorResponse payload = webClient.get()
                    .uri(pagamentoPath)
                    .retrieve()
                    .bodyToMono(ValidadorResponse.class)
                    .block();

            log.info("[sucesso] Validacao feita com sucesso.");
            return payload.getData().isAuthorized();
        } catch (WebClientResponseException e) {
            log.error("[error] Autorizador externo retornou erro: {}", e.getResponseBodyAsString());
            throw APIException.build(HttpStatus.BAD_REQUEST, e.getMessage());
        } finally {
            log.debug("[Finaliza] AutorizadorPagamentosInfra - validarAutorizacao");
        }
    }

    @Override
    public boolean validarCancelamento(ValidadorRequest validadorRequest) {
        log.info("[Inicia] AutorizadorPagamentosInfra - validarCancelamento");

        try {
            ValidadorResponse payload = webClient.get()
                    .uri(cancelamentoPath)
                    .retrieve()
                    .bodyToMono(ValidadorResponse.class)
                    .block();

            log.info("[sucesso] Cancelamento autorizado.");
            return payload.getData().isAuthorized();
        } catch (WebClientResponseException e) {
            log.error("[error] Autorizador externo retornou erro no cancelamento: {}", e.getResponseBodyAsString());
            throw APIException.build(HttpStatus.BAD_REQUEST, e.getMessage());
        } finally {
            log.debug("[Finaliza] AutorizadorPagamentosInfra - validarCancelamento");
        }
    }

}
