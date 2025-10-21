package com.pagamentos.nimble.nimble_pagamento.security.authorization.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Component;

import lombok.extern.log4j.Log4j2;

@Component
@Log4j2
public class LoginAttemptService {

    // Configuração: Máximo de 5 tentativas por usuário/IP em 15 minutos
    private static final int MAX_ATTEMPTS = 5;
    private static final long LOCK_TIME_MINUTES = 15;

    // Rastrear tentativas por chave (usuário ou IP)
    private final ConcurrentHashMap<String, LoginAttempt> attempts = new ConcurrentHashMap<>();

    public void loginBemSucedido(String chave) {
        attempts.remove(chave);
        log.info("Login bem-sucedido para chave: {}", chave);
    }

    public void loginFalhou(String chave) {
        LoginAttempt tentativa = attempts.computeIfAbsent(chave, k -> new LoginAttempt());
        tentativa.incrementar();

        if (tentativa.obterContagem() >= MAX_ATTEMPTS) {
            tentativa.bloquear();
            log.warn("Conta/IP bloqueada por excesso de tentativas: {}", chave);
        }
    }

    public boolean estaBloqueado(String chave) {
        LoginAttempt tentativa = attempts.get(chave);
        if (tentativa == null || !tentativa.estaBloqueado()) {
            return false;
        }

        // Verifica se o bloqueio expirou
        if (LocalDateTime.now().isAfter(tentativa.obterBloqueadoAte())) {
            attempts.remove(chave);
            return false;
        }

        return true;
    }

    public long obterTempoRestanteBloqueio(String chave) {
        LoginAttempt tentativa = attempts.get(chave);
        if (tentativa == null || !tentativa.estaBloqueado()) {
            return 0;
        }

        long restante = Duration.between(LocalDateTime.now(), tentativa.obterBloqueadoAte()).toMinutes();
        return Math.max(0, restante);
    }

    private static class LoginAttempt {
        private final AtomicInteger count = new AtomicInteger(0);
        private LocalDateTime lockedUntil;

        public void incrementar() {
            count.incrementAndGet();
        }

        public int obterContagem() {
            return count.get();
        }

        public void bloquear() {
            lockedUntil = LocalDateTime.now().plusMinutes(LOCK_TIME_MINUTES);
        }

        public boolean estaBloqueado() {
            return lockedUntil != null && LocalDateTime.now().isBefore(lockedUntil);
        }

        public LocalDateTime obterBloqueadoAte() {
            return lockedUntil;
        }
    }
}
