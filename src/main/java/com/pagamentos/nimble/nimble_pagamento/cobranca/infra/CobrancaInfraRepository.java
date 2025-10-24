package com.pagamentos.nimble.nimble_pagamento.cobranca.infra;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import com.pagamentos.nimble.nimble_pagamento.cobranca.application.repository.CobrancaRepository;
import com.pagamentos.nimble.nimble_pagamento.cobranca.domain.Cobranca;
import com.pagamentos.nimble.nimble_pagamento.cobranca.domain.StatusCobranca;
import com.pagamentos.nimble.nimble_pagamento.handler.APIException;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Repository
@Log4j2
@RequiredArgsConstructor
public class CobrancaInfraRepository implements CobrancaRepository {

    private final CobrancaSpringDataJPARepository cobrancaSpringDataJPARepository;

    @Override
    public Cobranca salvaCobranca(Cobranca cobranca) {
        log.info("[Inicia] CobrancaInfraRespository - salvaCobranca");
        try {
            cobrancaSpringDataJPARepository.save(cobranca);
        } catch (Exception e) {
            throw APIException.build(HttpStatus.BAD_REQUEST, "Cobranca ja cadastrada!");
        }
        log.info("[Finaliza] CobrancaInfraRespository - salvaCobranca");
        return cobranca;
    }

    @Override
    public Cobranca buscaCobrancaPorId(UUID idCobranca) {
        log.info("[Inicia] CobrancaInfraRespository - buscaCobrancaPorId");
        Cobranca cobranca = cobrancaSpringDataJPARepository
                .findById(idCobranca)
                .orElseThrow(() -> APIException.build(HttpStatus.NOT_FOUND, "Cobranca não encontrada"));
        log.info("[Finaliza] CobrancaInfraRespository - buscaCobrancaPorId");
        return cobranca;
    }

    @Override
    public Page<Cobranca> buscaCobrancasCriadas(UUID idUsuarioOriginador, Pageable pageable, StatusCobranca status) {
        log.info("[Inicia] CobrancaInfraRespository - buscaCobrancasCriadas");
        Page<Cobranca> cobrancas = cobrancaSpringDataJPARepository
                .findByIdUsuarioOriginador(idUsuarioOriginador, pageable, status);
        log.info("[Finaliza] CobrancaInfraRespository - buscaCobrancasCriadas");
        return cobrancas;
    }

    @Override
    public Page<Cobranca> buscaCobrancasRecebidas(String cpfLimpo, Pageable pageable, StatusCobranca status) {
        log.info("[Inicia] CobrancaInfraRespository - buscaCobrancasRecebidas");
        Page<Cobranca> cobrancas = cobrancaSpringDataJPARepository
                .findByCpfDestinatario(cpfLimpo, pageable, status);
        log.info("[Finaliza] CobrancaInfraRespository - buscaCobrancasRecebidas");
        return cobrancas;
    }

}
