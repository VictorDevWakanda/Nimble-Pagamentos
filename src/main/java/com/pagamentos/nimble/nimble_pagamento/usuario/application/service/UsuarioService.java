package com.pagamentos.nimble.nimble_pagamento.usuario.application.service;

import java.util.UUID;

import com.pagamentos.nimble.nimble_pagamento.usuario.application.api.UsuarioAlteracaoRequest;
import com.pagamentos.nimble.nimble_pagamento.usuario.application.api.UsuarioDetalhadoResponse;
import com.pagamentos.nimble.nimble_pagamento.usuario.application.api.UsuarioRequest;
import com.pagamentos.nimble.nimble_pagamento.usuario.application.api.UsuarioResponse;

import jakarta.validation.Valid;

public interface UsuarioService {

    UsuarioResponse criaUsuario(@Valid UsuarioRequest usuarioRequest);

    UsuarioDetalhadoResponse buscaUsuarioAtravesId(UUID idUsuario);

    void deletaUsuarioAtravesId(UUID idUsuario);

    void patchAlteraUsuario(UUID idUsuario, UsuarioAlteracaoRequest usuarioAlteracaoRequest);
}
