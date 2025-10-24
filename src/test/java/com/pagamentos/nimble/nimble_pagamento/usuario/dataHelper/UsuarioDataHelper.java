package com.pagamentos.nimble.nimble_pagamento.usuario.dataHelper;

import java.math.BigDecimal;

import com.pagamentos.nimble.nimble_pagamento.usuario.application.api.UsuarioAlteracaoRequest;
import com.pagamentos.nimble.nimble_pagamento.usuario.application.api.UsuarioDetalhadoResponse;
import com.pagamentos.nimble.nimble_pagamento.usuario.application.api.UsuarioRequest;
import com.pagamentos.nimble.nimble_pagamento.usuario.application.api.UsuarioResponse;
import com.pagamentos.nimble.nimble_pagamento.usuario.domain.Usuario;

public class UsuarioDataHelper {

    public static UsuarioRequest criarUsuarioRequestValido() {
        return new UsuarioRequest("João Silva", "12345678909", "joao@teste.com", "senha123");
    }

    public static UsuarioRequest criarUsuarioRequestInvalido() {
        return new UsuarioRequest("", "1234567890", "email-invalido", "");
    }

    public static UsuarioRequest criarUsuarioRequestCPFDuplicado() {
        return new UsuarioRequest("Maria Santos", "12345678909", "maria@teste.com", "senha123");
    }

    public static UsuarioRequest criarUsuarioRequestEmailDuplicado() {
        return new UsuarioRequest("Pedro Costa", "98765432109", "joao@teste.com", "senha123");
    }

    public static UsuarioRequest criarUsuarioRequestNomeVazio() {
        return new UsuarioRequest("", "55566677788", "nomevazio@teste.com", "senha123");
    }

    public static UsuarioRequest criarUsuarioRequestSenhaCurta() {
        return new UsuarioRequest("Ana Silva", "11122233344", "ana@teste.com", "123");
    }

    public static UsuarioAlteracaoRequest criarUsuarioAlteracaoRequestValido() {
        return new UsuarioAlteracaoRequest("João Silva Alterado", "joao.alterado@teste.com", "novaSenha123");
    }

    public static UsuarioAlteracaoRequest criarUsuarioAlteracaoRequestInvalido() {
        return new UsuarioAlteracaoRequest("", "email-invalido", "");
    }

    public static Usuario criarUsuario() {
        return new Usuario(criarUsuarioRequestValido(), "hashedPassword123");
    }

    public static Usuario criarUsuarioComSaldo(BigDecimal saldo) {
        Usuario usuario = new Usuario(criarUsuarioRequestValido(), "hashedPassword123");
        // Configurar saldo
        try {
            var saldoField = Usuario.class.getDeclaredField("saldo");
            saldoField.setAccessible(true);
            saldoField.set(usuario, saldo);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao configurar saldo do usuário", e);
        }
        return usuario;
    }

    public static Usuario criarUsuarioComSaldoZero() {
        return criarUsuarioComSaldo(BigDecimal.ZERO);
    }

    public static Usuario criarUsuarioComSaldoSuficiente() {
        return criarUsuarioComSaldo(new BigDecimal("1000.00"));
    }

    public static Usuario criarUsuarioComSaldoInsuficiente() {
        return criarUsuarioComSaldo(new BigDecimal("50.00"));
    }

    public static Usuario criarUsuarioSemSaldo() {
        return criarUsuarioComSaldo(BigDecimal.ZERO);
    }

    public static Usuario criarUsuarioParaAutenticacao() {
        return new Usuario(new UsuarioRequest("Login Teste", "99988877766", "login@teste.com", "senhaLogin"), "hashedLoginPassword");
    }

    public static UsuarioDetalhadoResponse criarUsuarioDetalhadoResponse() {
        Usuario usuario = criarUsuarioComSaldoSuficiente();
        return new UsuarioDetalhadoResponse(usuario);
    }

    public static UsuarioResponse criarUsuarioResponse() {
        Usuario usuario = criarUsuario();
        return new UsuarioResponse(usuario.getIdUsuario());
    }
}
