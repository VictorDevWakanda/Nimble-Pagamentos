package com.pagamentos.nimble.nimble_pagamento.security.authentication.service;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.pagamentos.nimble.nimble_pagamento.handler.APIException;
import com.pagamentos.nimble.nimble_pagamento.security.authentication.api.AutenticacaoResponseDto;
import com.pagamentos.nimble.nimble_pagamento.security.authentication.api.TokenType;
import com.pagamentos.nimble.nimble_pagamento.security.authentication.api.UsuarioLoginDto;
import com.pagamentos.nimble.nimble_pagamento.security.authorization.service.LoginAttemptService;
import com.pagamentos.nimble.nimble_pagamento.usuario.domain.Usuario;
import com.pagamentos.nimble.nimble_pagamento.usuario.infra.UsuarioSpringDataJPARepository;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class UsuarioLoginApplicationService implements UsuarioLoginService {
    private final UsuarioSpringDataJPARepository usuarioSpringDataJPARepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final LoginAttemptService loginAttemptService;

    @Override
    public AutenticacaoResponseDto autenticarUsuario(UsuarioLoginDto usuarioLoginDto) {
        String chave = obterChaveCliente();
        
        // Verifica se o usuário/IP está bloqueado
        if (loginAttemptService.estaBloqueado(chave)) {
            long minutosRestantes = loginAttemptService.obterTempoRestanteBloqueio(chave);
            log.warn("Tentativa de login bloqueada para chave: {}", chave);
            throw APIException.build(HttpStatus.LOCKED, 
                "Conta temporariamente bloqueada por excesso de tentativas. Tente novamente em " + 
                minutosRestantes + " minutos.");
        }
        
        try {
            Usuario usuario = buscarUsuarioPorCpfOuEmail(usuarioLoginDto.getCpfOuEmail());
            validarSenha(usuarioLoginDto.getSenha(), usuario.getSenhaHash());
            String token = tokenService.generateToken(usuario);
            
            // Registra sucesso e limpa tentativas
            loginAttemptService.loginBemSucedido(chave);
            
            return new AutenticacaoResponseDto(TokenType.BEARER, LocalDateTime.now().plusHours(2), token);
        } catch (APIException e) {
            // Registra tentativa falhada
            loginAttemptService.loginFalhou(chave);
            throw e;
        }
    }

    private String obterChaveCliente() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            HttpServletRequest request = attrs.getRequest();
            String xForwardedFor = request.getHeader("X-Forwarded-For");
            if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
                return xForwardedFor.split(",")[0].trim();
            }
            return request.getRemoteAddr();
        }
        return "unknown";
    }

    private Usuario buscarUsuarioPorCpfOuEmail(String cpfOuEmail) {
        return usuarioSpringDataJPARepository.findByCpf(cpfOuEmail)
                .or(() -> usuarioSpringDataJPARepository.findByEmail(cpfOuEmail))
                .orElseThrow(() -> APIException.build(HttpStatus.UNAUTHORIZED, "Usuário ou senha inválidos"));
    }

    private void validarSenha(String senhaRaw, String senhaHash) {
        if (!passwordEncoder.matches(senhaRaw, senhaHash)) {
            throw APIException.build(HttpStatus.UNAUTHORIZED, "Usuário ou senha inválidos");
        }
    }
}
