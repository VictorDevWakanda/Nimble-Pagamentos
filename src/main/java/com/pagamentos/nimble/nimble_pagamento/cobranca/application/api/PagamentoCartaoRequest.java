package com.pagamentos.nimble.nimble_pagamento.cobranca.application.api;

import lombok.Value;

@Value
public class PagamentoCartaoRequest {
    String numeroCartao;
    String validade;
    String cvv;
}
