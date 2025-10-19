package com.pagamentos.nimble.nimble_pagamento.usuario.application.repository;

import java.util.UUID;

import com.pagamentos.nimble.nimble_pagamento.usuario.domain.Usuario;

public interface UsuarioRepository {
    Usuario salvaUsuario(Usuario novoUsuario);

    Usuario buscaUsuarioAtravesId(UUID idUsuario);

    void deletaUsuario(Usuario usuario);
}
