package com.pagamentos.nimble.nimble_pagamento.cobranca.application.api;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.pagamentos.nimble.nimble_pagamento.cobranca.domain.Cobranca;
import com.pagamentos.nimble.nimble_pagamento.cobranca.domain.StatusCobranca;

import lombok.Value;

@Value
public class CobrancaDetalhadaResponse {

    private UUID idCobranca;
    private String cpfDestinatario;
    private BigDecimal valorCobranca;
    private String descricao;
    private StatusCobranca status;
    private LocalDateTime dataExpiracao;
    private LocalDateTime dataPagamento;
    private LocalDateTime dataCancelamento;

    public CobrancaDetalhadaResponse(Cobranca cobranca) {
        this.idCobranca = cobranca.getIdCobranca();
        this.cpfDestinatario = cobranca.getCpfDestinatario();
        this.valorCobranca = cobranca.getValorCobranca();
        this.descricao = cobranca.getDescricao();
        this.status = cobranca.getStatus();
        this.dataExpiracao = cobranca.getDataExpiracao();
        this.dataPagamento = cobranca.getDataPagamento();
        this.dataCancelamento = cobranca.getDataCancelamento();
    }
}
