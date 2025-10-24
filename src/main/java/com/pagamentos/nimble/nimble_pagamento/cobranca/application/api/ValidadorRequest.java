package com.pagamentos.nimble.nimble_pagamento.cobranca.application.api;

import java.math.BigDecimal;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ValidadorRequest {
    private UUID idCobranca;
    private String cpfPagador;
    private BigDecimal valor;
    private String numeroCartao;
    private String validade;
    private String cvv;

    public ValidadorRequest(UUID idCobranca, String cpfPagador, BigDecimal valor,
            String numeroCartao, String validade, String cvv) {
        this.idCobranca = idCobranca;
        this.cpfPagador = cpfPagador;
        this.valor = valor;
        this.numeroCartao = numeroCartao;
        this.validade = validade;
        this.cvv = cvv;
    }

    public static ValidadorRequest paraDeposito(String cpfPagador, BigDecimal valor) {
        return new ValidadorRequest(null, cpfPagador, valor, null, null, null);
    }

    public static ValidadorRequest paraPagamentoSaldo(UUID idCobranca, String cpfPagador, BigDecimal valor) {
        return new ValidadorRequest(idCobranca, cpfPagador, valor, null, null, null);
    }

    public static ValidadorRequest paraPagamentoCartao(UUID idCobranca, String cpfPagador, BigDecimal valor,
            String numeroCartao, String validade, String cvv) {
        return new ValidadorRequest(idCobranca, cpfPagador, valor, numeroCartao, validade, cvv);
    }

    public static ValidadorRequest paraCancelamento(UUID idCobranca, String cpfPagador, BigDecimal valor) {
        return new ValidadorRequest(idCobranca, cpfPagador, valor, null, null, null);
    }

}
