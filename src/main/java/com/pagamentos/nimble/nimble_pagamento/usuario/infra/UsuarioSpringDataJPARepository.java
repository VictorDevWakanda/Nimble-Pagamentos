package com.pagamentos.nimble.nimble_pagamento.usuario.infra;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pagamentos.nimble.nimble_pagamento.usuario.domain.Usuario;

public interface UsuarioSpringDataJPARepository extends JpaRepository<Usuario, UUID> {
    //TODO Implementar Queries!
}
