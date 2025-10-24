package com.pagamentos.nimble.nimble_pagamento.cobranca.infra;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import com.pagamentos.nimble.nimble_pagamento.cobranca.dataHelper.CobrancaDataHelper;
import com.pagamentos.nimble.nimble_pagamento.cobranca.domain.Cobranca;
import com.pagamentos.nimble.nimble_pagamento.cobranca.domain.StatusCobranca;
import com.pagamentos.nimble.nimble_pagamento.handler.APIException;

@ExtendWith(MockitoExtension.class)
public class CobrancaInfraRepositoryTest {

    @InjectMocks
    private CobrancaInfraRepository cobrancaInfraRepository;

    @Mock
    private CobrancaSpringDataJPARepository springDataRepository;

    private Cobranca cobranca;
    private UUID idCobranca;
    private UUID usuarioOriginadorId;

    @BeforeEach
    void setUp() {
        idCobranca = UUID.randomUUID();
        usuarioOriginadorId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000"); // ID fixo usado no DataHelper
        cobranca = CobrancaDataHelper.criarCobrancaPendente(idCobranca);
    }

    @Test
    @DisplayName("Deve salvar cobrança com sucesso")
    void deveSalvarCobrancaComSucesso() {
        when(springDataRepository.save(cobranca)).thenReturn(cobranca);

        Cobranca resultado = cobrancaInfraRepository.salvaCobranca(cobranca);

        assertEquals(cobranca, resultado);
        verify(springDataRepository, times(1)).save(cobranca);
    }

    @Test
    @DisplayName("Deve lançar exceção ao salvar cobrança duplicada")
    void deveLancarExcecaoAoSalvarCobrancaDuplicada() {
        when(springDataRepository.save(cobranca))
                .thenThrow(new org.springframework.dao.DataIntegrityViolationException("Cobranca já existe!"));

        APIException ex = assertThrows(APIException.class, () -> cobrancaInfraRepository.salvaCobranca(cobranca));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusException());
        assertEquals("Cobranca ja cadastrada!", ex.getMessage());
        verify(springDataRepository, times(1)).save(cobranca);
    }

    @Test
    @DisplayName("Deve buscar cobrança por ID com sucesso")
    void deveBuscarCobrancaPorIdComSucesso() {
        when(springDataRepository.findById(idCobranca)).thenReturn(Optional.of(cobranca));

        Cobranca resultado = cobrancaInfraRepository.buscaCobrancaPorId(idCobranca);

        assertEquals(cobranca, resultado);
        verify(springDataRepository, times(1)).findById(idCobranca);
    }

    @Test
    @DisplayName("Deve lançar exceção quando cobrança não encontrada")
    void deveLancarExcecaoQuandoCobrancaNaoEncontrada() {
        when(springDataRepository.findById(idCobranca)).thenReturn(Optional.empty());

        APIException ex = assertThrows(APIException.class,
                () -> cobrancaInfraRepository.buscaCobrancaPorId(idCobranca));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusException());
        assertEquals("Cobranca não encontrada", ex.getMessage());
        verify(springDataRepository, times(1)).findById(idCobranca);
    }

    @Test
    @DisplayName("Deve buscar cobranças criadas com sucesso")
    void deveBuscarCobrancasCriadasComSucesso() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Cobranca> cobrancasList = List.of(cobranca);
        Page<Cobranca> cobrancasPage = new PageImpl<>(cobrancasList, pageable, 1);

        when(springDataRepository.findByIdUsuarioOriginador(usuarioOriginadorId, pageable, StatusCobranca.PENDENTE))
                .thenReturn(cobrancasPage);

        Page<Cobranca> resultado = cobrancaInfraRepository.buscaCobrancasCriadas(usuarioOriginadorId, pageable,
                StatusCobranca.PENDENTE);

        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        assertEquals(cobranca, resultado.getContent().get(0));
        verify(springDataRepository, times(1)).findByIdUsuarioOriginador(usuarioOriginadorId, pageable,
                StatusCobranca.PENDENTE);
    }

    @Test
    @DisplayName("Deve buscar cobranças recebidas com sucesso")
    void deveBuscarCobrancasRecebidasComSucesso() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Cobranca> cobrancasList = List.of(cobranca);
        Page<Cobranca> cobrancasPage = new PageImpl<>(cobrancasList, pageable, 1);

        when(springDataRepository.findByCpfDestinatario("12345678909", pageable, StatusCobranca.PAGA))
                .thenReturn(cobrancasPage);

        Page<Cobranca> resultado = cobrancaInfraRepository.buscaCobrancasRecebidas("12345678909", pageable,
                StatusCobranca.PAGA);

        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        assertEquals(cobranca, resultado.getContent().get(0));
        verify(springDataRepository, times(1)).findByCpfDestinatario("12345678909", pageable, StatusCobranca.PAGA);
    }

    @Test
    @DisplayName("Deve buscar cobrança por ID e usuário originador com sucesso")
    void deveBuscarCobrancaPorIdEUsuarioOriginadorComSucesso() {
        when(springDataRepository.findById(idCobranca)).thenReturn(Optional.of(cobranca));

        Cobranca resultado = cobrancaInfraRepository.buscaCobrancaPorId(idCobranca);

        assertEquals(cobranca, resultado);
        assertTrue(resultado.pertenceAoOriginador(usuarioOriginadorId));
        verify(springDataRepository, times(1)).findById(idCobranca);
    }

    @Test
    @DisplayName("Deve retornar Optional vazio quando cobrança não encontrada por ID e usuário originador")
    void deveRetornarOptionalVazioQuandoCobrancaNaoEncontradaPorIdEUsuarioOriginador() {
        when(springDataRepository.findById(idCobranca)).thenReturn(Optional.empty());

        APIException ex = assertThrows(APIException.class,
                () -> cobrancaInfraRepository.buscaCobrancaPorId(idCobranca));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusException());
        assertEquals("Cobranca não encontrada", ex.getMessage());
        verify(springDataRepository, times(1)).findById(idCobranca);
    }

    @Test
    @DisplayName("Deve buscar cobrança por ID e CPF destinatário com sucesso")
    void deveBuscarCobrancaPorIdECpfDestinatarioComSucesso() {
        when(springDataRepository.findById(idCobranca)).thenReturn(Optional.of(cobranca));

        Cobranca resultado = cobrancaInfraRepository.buscaCobrancaPorId(idCobranca);

        assertEquals(cobranca, resultado);
        assertTrue(resultado.ehDestinatario("12345678909"));
        verify(springDataRepository, times(1)).findById(idCobranca);
    }

    @Test
    @DisplayName("Deve retornar Optional vazio quando cobrança não encontrada por ID e CPF destinatário")
    void deveRetornarOptionalVazioQuandoCobrancaNaoEncontradaPorIdECpfDestinatario() {
        when(springDataRepository.findById(idCobranca)).thenReturn(Optional.empty());

        APIException ex = assertThrows(APIException.class,
                () -> cobrancaInfraRepository.buscaCobrancaPorId(idCobranca));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusException());
        assertEquals("Cobranca não encontrada", ex.getMessage());
        verify(springDataRepository, times(1)).findById(idCobranca);
    }
}
