package com.pagamentos.nimble.nimble_pagamento.security.authentication.filter;

import java.io.IOException;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pagamentos.nimble.nimble_pagamento.handler.APIException;
import com.pagamentos.nimble.nimble_pagamento.security.authentication.service.TokenService;
import com.pagamentos.nimble.nimble_pagamento.security.authentication.service.UsuarioDetails;
import com.pagamentos.nimble.nimble_pagamento.usuario.domain.Usuario;
import com.pagamentos.nimble.nimble_pagamento.usuario.infra.UsuarioSpringDataJPARepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Component
@RequiredArgsConstructor
@Log4j2
public class SecurityFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final UsuarioSpringDataJPARepository usuarioSpringDataJPARepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = extrairToken(request);
            if (token != null) {
                String idUsuario = tokenService.validateToken(token);
                if (idUsuario == null) {
                    throw APIException.build(HttpStatus.UNAUTHORIZED, "Token inválido ou expirado");
                }

                Usuario usuario = buscarUsuarioAutenticado(idUsuario);
                autenticarUsuario(usuario);
            }

            filterChain.doFilter(request, response);

        } catch (APIException e) {
            log.warn("Autenticação falhou: {}", e.getMessage());
            response.setStatus(e.getStatusException().value());
            response.setContentType("application/json");
            response.getWriter().write(objectMapper.writeValueAsString(e.getBodyException()));
        }
    }

    private String extrairToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        return (authHeader != null && authHeader.startsWith("Bearer "))
                ? authHeader.replace("Bearer ", "")
                : null;
    }

    private Usuario buscarUsuarioAutenticado(String idUsuario) {
        return usuarioSpringDataJPARepository.findById(UUID.fromString(idUsuario))
                .orElseThrow(() -> APIException.build(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
    }

    private void autenticarUsuario(Usuario usuario) {
        UsuarioDetails usuarioDetails = new UsuarioDetails(usuario);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                usuarioDetails, null, usuarioDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

}
