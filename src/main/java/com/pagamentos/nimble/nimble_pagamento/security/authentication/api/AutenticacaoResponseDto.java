package com.pagamentos.nimble.nimble_pagamento.security.authentication.api;

import java.time.LocalDateTime;


public record AutenticacaoResponseDto(TokenType type, LocalDateTime expiracao, String token) {
}
