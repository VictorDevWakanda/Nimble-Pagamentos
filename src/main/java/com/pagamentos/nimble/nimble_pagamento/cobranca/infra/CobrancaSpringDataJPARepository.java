package com.pagamentos.nimble.nimble_pagamento.cobranca.infra;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pagamentos.nimble.nimble_pagamento.cobranca.domain.Cobranca;
import com.pagamentos.nimble.nimble_pagamento.cobranca.domain.StatusCobranca;

public interface CobrancaSpringDataJPARepository extends JpaRepository<Cobranca, UUID> {

    @Query("SELECT c FROM Cobranca c WHERE c.idUsuarioOriginador = :idusuarioOriginador")
    Page<Cobranca> findByIdUsuarioOriginador(UUID idusuarioOriginador, Pageable pageable, StatusCobranca status);

    @Query("SELECT c FROM Cobranca c WHERE c.cpfDestinatario = :cpfDestinatario")
    Page<Cobranca> findByCpfDestinatario(@Param("cpfDestinatario") String cpfLimpo,
            Pageable pageable,
            @Param("status") StatusCobranca status);

}
