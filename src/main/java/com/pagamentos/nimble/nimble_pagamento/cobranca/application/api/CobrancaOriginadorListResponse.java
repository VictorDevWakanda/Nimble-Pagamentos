package com.pagamentos.nimble.nimble_pagamento.cobranca.application.api;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.pagamentos.nimble.nimble_pagamento.cobranca.domain.Cobranca;

import lombok.Value;

@Value
public class CobrancaOriginadorListResponse {
    private String cpfOriginador;
    private BigDecimal valorCobranca;
    private String descricao;
    private LocalDateTime dataExpiracao;

    public CobrancaOriginadorListResponse(String cpfOriginador, Cobranca cobranca) {
        this.cpfOriginador = cpfOriginador;
        this.valorCobranca = cobranca.getValorCobranca();
        this.descricao = cobranca.getDescricao();
        this.dataExpiracao = cobranca.getDataExpiracao();
    }
}
