package com.pagamentos.nimble.nimble_pagamento.security.authentication.service;

import com.pagamentos.nimble.nimble_pagamento.security.authentication.api.AutenticacaoResponseDto;
import com.pagamentos.nimble.nimble_pagamento.security.authentication.api.UsuarioLoginDto;

public interface UsuarioLoginService {

    AutenticacaoResponseDto autenticarUsuario(UsuarioLoginDto usuarioLoginDto);
}
