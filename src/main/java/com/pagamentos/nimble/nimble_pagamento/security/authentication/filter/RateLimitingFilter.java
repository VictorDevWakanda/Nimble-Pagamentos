package com.pagamentos.nimble.nimble_pagamento.security.authentication.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pagamentos.nimble.nimble_pagamento.handler.ErrorApiResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
@Log4j2
public class RateLimitingFilter extends OncePerRequestFilter {

    // Rastrear tentativas por IP
    private final ConcurrentHashMap<String, RequestBucket> buckets = new ConcurrentHashMap<>();

    // Configuração: Máximo de 5 requisições por minuto por IP
    private static final int MAX_REQUESTS_PER_MINUTE = 5;
    private static final long WINDOW_SIZE_MS = 60 * 1000; // 1 minuto

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String clientIP = getClienteIP(request);
        RequestBucket bucket = buckets.computeIfAbsent(clientIP, k -> new RequestBucket());

        synchronized (bucket) {
            long now = System.currentTimeMillis();
            bucket.requests.removeIf(timestamp -> now - timestamp > WINDOW_SIZE_MS);

            if (bucket.requests.size() >= MAX_REQUESTS_PER_MINUTE) {
                log.warn("Rate limit excedido para IP: {}", clientIP);
                enviarRespostaLimiteTaxa(response);
                return;
            }

            bucket.requests.add(now);
        }

        filterChain.doFilter(request, response);
    }

    private String getClienteIP(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private void enviarRespostaLimiteTaxa(HttpServletResponse response) throws IOException {
        response.setStatus(429);
        response.setContentType("application/json");
        ErrorApiResponse errorResponse = ErrorApiResponse.builder()
                .message("Muitas requisições. Tente novamente em breve.")
                .description("Rate limit excedido.")
                .build();
        ObjectMapper mapper = new ObjectMapper();
        response.getWriter().write(mapper.writeValueAsString(errorResponse));
    }

    private static class RequestBucket {
        final ConcurrentLinkedQueue<Long> requests = new ConcurrentLinkedQueue<>();
    }
}
