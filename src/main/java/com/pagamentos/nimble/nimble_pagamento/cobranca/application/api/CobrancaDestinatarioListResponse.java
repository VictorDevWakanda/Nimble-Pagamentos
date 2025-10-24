package com.pagamentos.nimble.nimble_pagamento.cobranca.application.api;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import com.pagamentos.nimble.nimble_pagamento.cobranca.domain.Cobranca;

import lombok.Value;

@Value
public class CobrancaDestinatarioListResponse {
    private String cpfDestinatario;
    private BigDecimal valorCobranca;
    private String descricao;
    private LocalDateTime dataExpiracao;

    public CobrancaDestinatarioListResponse(Cobranca cobranca) {
        this.cpfDestinatario = cobranca.getCpfDestinatario();
        this.valorCobranca = cobranca.getValorCobranca();
        this.descricao = cobranca.getDescricao();
        this.dataExpiracao = cobranca.getDataExpiracao();
    }

    public static Page<CobrancaDestinatarioListResponse> converte(Page<Cobranca> cobrancas) {
        List<CobrancaDestinatarioListResponse> cobrancasList = cobrancas.stream()
                .map(CobrancaDestinatarioListResponse::new)
                .collect(Collectors.toList());
        return new PageImpl<>(cobrancasList, cobrancas.getPageable(), cobrancas.getTotalElements());
    }
}
