package com.pagamentos.nimble.nimble_pagamento.usuario.infra;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pagamentos.nimble.nimble_pagamento.usuario.domain.Usuario;

public interface UsuarioSpringDataJPARepository extends JpaRepository<Usuario, UUID> {
    Optional<Usuario> findByCpf(String cpf);

    Optional<Usuario> findByEmail(String email);
}
