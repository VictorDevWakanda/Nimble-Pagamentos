
package com.pagamentos.nimble.nimble_pagamento.cobranca.dataHelper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.pagamentos.nimble.nimble_pagamento.cobranca.application.api.CobrancaDestinatarioListResponse;
import com.pagamentos.nimble.nimble_pagamento.cobranca.application.api.CobrancaRequest;
import com.pagamentos.nimble.nimble_pagamento.cobranca.application.api.PagamentoCartaoRequest;
import com.pagamentos.nimble.nimble_pagamento.cobranca.domain.Cobranca;
import com.pagamentos.nimble.nimble_pagamento.cobranca.domain.FormaPagamento;
import com.pagamentos.nimble.nimble_pagamento.cobranca.domain.StatusCobranca;
import com.pagamentos.nimble.nimble_pagamento.usuario.application.api.UsuarioRequest;
import com.pagamentos.nimble.nimble_pagamento.usuario.domain.Usuario;

public class CobrancaDataHelper {

    public static CobrancaRequest criarCobrancaRequestValida() {
        return new CobrancaRequest("12345678909", new BigDecimal("150.00"), "Serviço de consultoria");
    }

    public static CobrancaRequest criarCobrancaRequestInvalida() {
        return new CobrancaRequest("1234567890", new BigDecimal("0.00"), "Teste");
    }

    public static CobrancaRequest criarCobrancaRequestValorNegativo() {
        return new CobrancaRequest("12345678909", new BigDecimal("-50.00"), "Teste valor negativo");
    }

    public static CobrancaRequest criarCobrancaRequestCPFDuplicado() {
        return new CobrancaRequest("12345678909", new BigDecimal("100.00"), "Cobrança duplicada");
    }

    public static PagamentoCartaoRequest criarPagamentoCartaoRequestValido() {
        return new PagamentoCartaoRequest("4111111111111111", "12/28", "123");
    }

    public static PagamentoCartaoRequest criarPagamentoCartaoRequestInvalido() {
        return new PagamentoCartaoRequest("1234567890123456", "13/30", "12");
    }

    public static Cobranca criarCobranca() {
        return criarCobrancaComId(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"));
    }

    public static Cobranca criarCobranca(UUID idCobranca) {
        return criarCobrancaComId(idCobranca);
    }

    public static Cobranca criarCobrancaComId(UUID idCobranca) {
        Cobranca cobranca = new Cobranca(
                UUID.fromString("550e8400-e29b-41d4-a716-446655440000"), // idUsuarioOriginador
                criarCobrancaRequestValida());

        // Usar reflection para definir o ID específico
        try {
            var idField = Cobranca.class.getDeclaredField("idCobranca");
            idField.setAccessible(true);
            idField.set(cobranca, idCobranca);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao configurar ID da cobrança", e);
        }

        return cobranca;
    }

    public static void configurarIdCobranca(Cobranca cobranca, UUID idCobranca) {
        try {
            var idField = Cobranca.class.getDeclaredField("idCobranca");
            idField.setAccessible(true);
            idField.set(cobranca, idCobranca);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao configurar ID da cobrança", e);
        }
    }

    public static Cobranca criarCobrancaPaga() {
        Cobranca cobranca = criarCobranca();
        cobranca = mockCobrancaPaga(cobranca);
        return cobranca;
    }

    public static Cobranca criarCobrancaCancelada() {
        Cobranca cobranca = criarCobranca();
        cobranca = mockCobrancaCancelada(cobranca);
        return cobranca;
    }

    public static Cobranca criarCobrancaExpirada() {
        Cobranca cobranca = criarCobranca();
        // Simular data de expiração no passado
        return cobranca;
    }

    public static Usuario criarUsuarioOriginador() {
        return new Usuario(
                new UsuarioRequest("João Originador", "12345678909", "originador@teste.com", "senha123"),
                "hashedPassword123");
    }

    public static Usuario criarUsuarioDestinatario() {
        return new Usuario(
                new UsuarioRequest("Maria Destinatária", "11144477735", "destinataria@teste.com", "senha123"),
                "hashedPassword123");
    }

    public static Usuario criarUsuarioSemSaldo() {
        Usuario usuario = new Usuario(
                new UsuarioRequest("Carlos Sem Saldo", "98765432109", "semSaldo@teste.com", "senha123"),
                "hashedPassword123");
        // Configurar saldo zero
        try {
            var saldoField = Usuario.class.getDeclaredField("saldo");
            saldoField.setAccessible(true);
            saldoField.set(usuario, BigDecimal.ZERO);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao configurar saldo do usuário", e);
        }
        return usuario;
    }

    public static Page<CobrancaDestinatarioListResponse> criarPaginaCobrancaDestinatarioListResponse() {
        Cobranca cobranca = criarCobranca();
        List<CobrancaDestinatarioListResponse> lista = Arrays.asList(
                new CobrancaDestinatarioListResponse(cobranca));
        return new PageImpl<>(lista, Pageable.unpaged(), 1);
    }

    // Métodos auxiliares para mock
    private static Cobranca mockCobrancaPaga(Cobranca cobranca) {
        try {
            var field = Cobranca.class.getDeclaredField("status");
            field.setAccessible(true);
            field.set(cobranca, StatusCobranca.PAGA);

            var dataPagamentoField = Cobranca.class.getDeclaredField("dataPagamento");
            dataPagamentoField.setAccessible(true);
            dataPagamentoField.set(cobranca, LocalDateTime.now());

            var formaPagamentoField = Cobranca.class.getDeclaredField("formaPagamento");
            formaPagamentoField.setAccessible(true);
            formaPagamentoField.set(cobranca, FormaPagamento.SALDO);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao configurar mock de cobrança paga", e);
        }
        return cobranca;
    }

    private static Cobranca mockCobrancaCancelada(Cobranca cobranca) {
        try {
            var field = Cobranca.class.getDeclaredField("status");
            field.setAccessible(true);
            field.set(cobranca, StatusCobranca.CANCELADA);

            var dataCancelamentoField = Cobranca.class.getDeclaredField("dataCancelamento");
            dataCancelamentoField.setAccessible(true);
            dataCancelamentoField.set(cobranca, LocalDateTime.now());
        } catch (Exception e) {
            throw new RuntimeException("Erro ao configurar mock de cobrança cancelada", e);
        }
        return cobranca;
    }

    public static Cobranca criarCobrancaPendente(UUID idCobranca) {
        Cobranca cobranca = criarCobrancaComId(idCobranca);
        try {
            var statusField = Cobranca.class.getDeclaredField("status");
            statusField.setAccessible(true);
            statusField.set(cobranca, StatusCobranca.PENDENTE);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao configurar status da cobrança", e);
        }
        return cobranca;
    }
}
