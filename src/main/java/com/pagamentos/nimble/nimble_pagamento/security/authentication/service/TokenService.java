package com.pagamentos.nimble.nimble_pagamento.security.authentication.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.pagamentos.nimble.nimble_pagamento.handler.APIException;
import com.pagamentos.nimble.nimble_pagamento.usuario.domain.Usuario;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class TokenService {

    @Value("${security.token.jwt.secret}")  
    private String secret;

    @Value("${security.token.jwt.expiration}")
    private Long expiration;

    public String generateToken(Usuario usuario) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("nimble-pagamento")
                    .withSubject(usuario.getIdUsuario().toString())
                    .withClaim("nome", usuario.getNome())
                    .withClaim("cpf", usuario.getCpf())
                    .withClaim("email", usuario.getEmail())
                    .withExpiresAt(gerarTempoExpiracao())
                    .sign(algorithm);
        } catch (JWTCreationException exception) {
            throw APIException.build(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Erro ao gerar token: " + exception.getMessage());
        }
    }

    public String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("nimble-pagamento")
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException exception) {
            log.error("Erro ao validar token: " + exception.getMessage());
            return null;
        }
    }

    private Instant gerarTempoExpiracao() {
        return LocalDateTime.now().plusHours(expiration).toInstant(ZoneOffset.of("-03:00"));
    }
}
