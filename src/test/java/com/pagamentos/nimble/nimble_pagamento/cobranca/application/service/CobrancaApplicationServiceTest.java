package com.pagamentos.nimble.nimble_pagamento.cobranca.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.pagamentos.nimble.nimble_pagamento.autorizador.gateway.AutorizadorPagamentos;
import com.pagamentos.nimble.nimble_pagamento.cobranca.application.api.CobrancaDestinatarioListResponse;
import com.pagamentos.nimble.nimble_pagamento.cobranca.application.api.CobrancaDetalhadaResponse;
import com.pagamentos.nimble.nimble_pagamento.cobranca.application.api.CobrancaRequest;
import com.pagamentos.nimble.nimble_pagamento.cobranca.application.api.PagamentoCartaoRequest;
import com.pagamentos.nimble.nimble_pagamento.cobranca.application.api.ValidadorRequest;
import com.pagamentos.nimble.nimble_pagamento.cobranca.application.repository.CobrancaRepository;
import com.pagamentos.nimble.nimble_pagamento.cobranca.dataHelper.CobrancaDataHelper;
import com.pagamentos.nimble.nimble_pagamento.cobranca.domain.Cobranca;
import com.pagamentos.nimble.nimble_pagamento.cobranca.domain.StatusCobranca;
import com.pagamentos.nimble.nimble_pagamento.handler.APIException;
import com.pagamentos.nimble.nimble_pagamento.usuario.application.repository.UsuarioRepository;
import com.pagamentos.nimble.nimble_pagamento.usuario.domain.Usuario;

@ExtendWith(MockitoExtension.class)
public class CobrancaApplicationServiceTest {

    @InjectMocks
    private CobrancaApplicationService cobrancaApplicationService;

    @Mock
    private CobrancaRepository cobrancaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private AutorizadorPagamentos autorizadorPagamentos;

    private UUID idUsuarioOriginador;
    private UUID idUsuarioDestinatario;
    private UUID idCobranca;
    private CobrancaRequest requestValida;
    private Usuario usuarioOriginador;
    private Usuario usuarioDestinatario;
    private Cobranca cobranca;

    @BeforeEach
    void setUp() {
        idUsuarioOriginador = UUID.randomUUID();
        idUsuarioDestinatario = UUID.randomUUID();
        idCobranca = UUID.randomUUID();

        requestValida = CobrancaDataHelper.criarCobrancaRequestValida();

        usuarioOriginador = CobrancaDataHelper.criarUsuarioOriginador();
        usuarioDestinatario = CobrancaDataHelper.criarUsuarioDestinatario();
        cobranca = CobrancaDataHelper.criarCobrancaPendente(idCobranca);
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar cobrança com usuário originador inexistente")
    void criaCobrancaComUsuarioOriginadorInexistente() {
        when(usuarioRepository.buscaUsuarioAtravesId(idUsuarioOriginador)).thenReturn(null);

        APIException exception = assertThrows(APIException.class, () -> {
            cobrancaApplicationService.criaCobranca(idUsuarioOriginador, requestValida);
        });

        assertEquals("Usuário originador não encontrado.", exception.getMessage());
        verify(cobrancaRepository, never()).salvaCobranca(any());
    }

    @Test
    @DisplayName("Deve buscar cobrança por ID com sucesso")
    void buscaCobrancaPorIdComSucesso() {
        when(cobrancaRepository.buscaCobrancaPorId(idCobranca)).thenReturn(cobranca);
        when(usuarioRepository.buscaUsuarioAtravesId(idUsuarioOriginador)).thenReturn(usuarioOriginador);

        CobrancaDetalhadaResponse response = cobrancaApplicationService.buscaCobrancaPorId(idCobranca,
                idUsuarioOriginador);

        assertNotNull(response);
        verify(cobrancaRepository).buscaCobrancaPorId(idCobranca);
        verify(usuarioRepository).buscaUsuarioAtravesId(idUsuarioOriginador);
    }

    @Test
    @DisplayName("Deve listar cobranças criadas com sucesso")
    void listaCobrancasCriadasComSucesso() {
        Page<Cobranca> cobrancasPage = new PageImpl<>(List.of(cobranca));
        when(cobrancaRepository.buscaCobrancasCriadas(idUsuarioOriginador, Pageable.unpaged(), StatusCobranca.PENDENTE))
                .thenReturn(cobrancasPage);

        Page<CobrancaDestinatarioListResponse> response = cobrancaApplicationService
                .listaCobrancasCriadas(idUsuarioOriginador, Pageable.unpaged(), StatusCobranca.PENDENTE);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        verify(cobrancaRepository).buscaCobrancasCriadas(idUsuarioOriginador, Pageable.unpaged(),
                StatusCobranca.PENDENTE);
    }

    @Test
    @DisplayName("Deve efetuar pagamento com cartão com sucesso")
    void efetuaPagamentoComCartaoComSucesso() {
        PagamentoCartaoRequest pagamentoCartaoRequest = CobrancaDataHelper.criarPagamentoCartaoRequestValido();

        when(cobrancaRepository.buscaCobrancaPorId(idCobranca)).thenReturn(cobranca);
        when(usuarioRepository.buscaUsuarioAtravesId(idUsuarioDestinatario)).thenReturn(usuarioDestinatario);
        when(usuarioRepository.buscaUsuarioAtravesId(cobranca.getIdUsuarioOriginador())).thenReturn(usuarioOriginador);
        when(autorizadorPagamentos.validarAutorizacao(any(ValidadorRequest.class))).thenReturn(true);
        when(cobrancaRepository.salvaCobranca(cobranca)).thenReturn(cobranca);
        when(usuarioRepository.salvaUsuario(usuarioOriginador)).thenReturn(usuarioOriginador);

        cobrancaApplicationService.efetuaPagamentoComCartao(idCobranca, idUsuarioDestinatario, pagamentoCartaoRequest);

        verify(cobrancaRepository).buscaCobrancaPorId(idCobranca);
        verify(usuarioRepository).buscaUsuarioAtravesId(idUsuarioDestinatario);
        verify(usuarioRepository).buscaUsuarioAtravesId(cobranca.getIdUsuarioOriginador());
        verify(autorizadorPagamentos).validarAutorizacao(any(ValidadorRequest.class));
        verify(cobrancaRepository).salvaCobranca(cobranca);
        verify(usuarioRepository).salvaUsuario(usuarioOriginador);
    }
}
