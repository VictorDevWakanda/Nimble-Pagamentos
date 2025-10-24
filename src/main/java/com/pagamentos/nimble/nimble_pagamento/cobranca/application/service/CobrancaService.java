package com.pagamentos.nimble.nimble_pagamento.cobranca.application.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.pagamentos.nimble.nimble_pagamento.cobranca.application.api.CobrancaDestinatarioListResponse;
import com.pagamentos.nimble.nimble_pagamento.cobranca.application.api.CobrancaDetalhadaResponse;
import com.pagamentos.nimble.nimble_pagamento.cobranca.application.api.CobrancaOriginadorListResponse;
import com.pagamentos.nimble.nimble_pagamento.cobranca.application.api.CobrancaRequest;
import com.pagamentos.nimble.nimble_pagamento.cobranca.application.api.CobrancaResponse;
import com.pagamentos.nimble.nimble_pagamento.cobranca.application.api.DepositoRequest;
import com.pagamentos.nimble.nimble_pagamento.cobranca.application.api.PagamentoCartaoRequest;
import com.pagamentos.nimble.nimble_pagamento.cobranca.domain.StatusCobranca;

public interface CobrancaService {

        CobrancaResponse criaCobranca(UUID IdUsuarioOriginador, CobrancaRequest cobrancaRequest);

        CobrancaDetalhadaResponse buscaCobrancaPorId(UUID idCobranca, UUID idUsuarioSolicitante);

        Page<CobrancaDestinatarioListResponse> listaCobrancasCriadas(UUID IdusuarioOriginador, Pageable pageable,
                        StatusCobranca status);

        Page<CobrancaOriginadorListResponse> listaCobrancasRecebidas(UUID idUsuarioSolicitante, Pageable pageable,
                        StatusCobranca status);

        void efetuaPagamento(UUID idCobranca, UUID idUsuarioPagador);

        void efetuaPagamentoComCartao(UUID idCobranca, UUID idUsuarioPagador,
                        PagamentoCartaoRequest pagamentoCartaoRequest);

        void cancelaCobranca(UUID idCobranca, UUID idUsuarioSolicitante);

        void realizaDeposito(UUID idUsuario, DepositoRequest depositoRequest);

}
