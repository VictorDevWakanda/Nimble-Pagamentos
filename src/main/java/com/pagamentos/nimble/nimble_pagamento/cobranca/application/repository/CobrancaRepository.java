package com.pagamentos.nimble.nimble_pagamento.cobranca.application.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.pagamentos.nimble.nimble_pagamento.cobranca.domain.Cobranca;
import com.pagamentos.nimble.nimble_pagamento.cobranca.domain.StatusCobranca;

public interface CobrancaRepository {

    Cobranca salvaCobranca(Cobranca cobranca);

    Cobranca buscaCobrancaPorId(UUID idCobranca);

    Page<Cobranca> buscaCobrancasCriadas(UUID idUsuarioOriginador, Pageable pageable, StatusCobranca status);

    Page<Cobranca> buscaCobrancasRecebidas(String cpfLimpo, Pageable pageable, StatusCobranca status);
}
