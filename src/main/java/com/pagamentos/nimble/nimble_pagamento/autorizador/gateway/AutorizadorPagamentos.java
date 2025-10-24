package com.pagamentos.nimble.nimble_pagamento.autorizador.gateway;

import com.pagamentos.nimble.nimble_pagamento.cobranca.application.api.ValidadorRequest;

public interface AutorizadorPagamentos {

    boolean validarAutorizacao(ValidadorRequest validaCobrancaDTO);

    boolean validarCancelamento(ValidadorRequest validadorRequest);
}
