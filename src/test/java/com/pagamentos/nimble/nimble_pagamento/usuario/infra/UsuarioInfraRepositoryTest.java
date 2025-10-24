package com.pagamentos.nimble.nimble_pagamento.usuario.infra;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;

import com.pagamentos.nimble.nimble_pagamento.handler.APIException;
import com.pagamentos.nimble.nimble_pagamento.usuario.dataHelper.UsuarioDataHelper;
import com.pagamentos.nimble.nimble_pagamento.usuario.domain.Usuario;

@ExtendWith(MockitoExtension.class)
public class UsuarioInfraRepositoryTest {

    @InjectMocks
    private UsuarioInfraRepository usuarioInfraRepository;

    @Mock
    private UsuarioSpringDataJPARepository usuarioSpringDataJPARepository;

    private Usuario usuario;
    private UUID usuarioId;
    private String cpfUsuario;

    @BeforeEach
    void setUp() {
        usuarioId = UUID.randomUUID();
        cpfUsuario = "12345678909";
        usuario = UsuarioDataHelper.criarUsuario();
    }

    @Test
    @DisplayName("Deve salvar usuário com sucesso")
    void deveSalvarUsuarioComSucesso() {
        when(usuarioSpringDataJPARepository.save(usuario)).thenReturn(usuario);

        Usuario resultado = usuarioInfraRepository.salvaUsuario(usuario);

        assertNotNull(resultado);
        assertEquals(usuario, resultado);
        verify(usuarioSpringDataJPARepository, times(1)).save(usuario);
    }

    @Test
    @DisplayName("Deve lançar exceção ao salvar usuário com CPF duplicado")
    void deveLancarExcecaoAoSalvarUsuarioComCPFDuplicado() {
        when(usuarioSpringDataJPARepository.save(usuario))
                .thenThrow(new DataIntegrityViolationException("CPF duplicado"));

        APIException ex = assertThrows(APIException.class, () -> usuarioInfraRepository.salvaUsuario(usuario));

        assertEquals(HttpStatus.CONFLICT, ex.getStatusException());
        assertEquals("Usuario já existe!", ex.getMessage());
        verify(usuarioSpringDataJPARepository, times(1)).save(usuario);
    }

    @Test
    @DisplayName("Deve lançar exceção ao salvar usuário com email duplicado")
    void deveLancarExcecaoAoSalvarUsuarioComEmailDuplicado() {
        when(usuarioSpringDataJPARepository.save(usuario))
                .thenThrow(new DataIntegrityViolationException("Email duplicado"));

        APIException ex = assertThrows(APIException.class, () -> usuarioInfraRepository.salvaUsuario(usuario));

        assertEquals(HttpStatus.CONFLICT, ex.getStatusException());
        assertEquals("Usuario já existe!", ex.getMessage());
        verify(usuarioSpringDataJPARepository, times(1)).save(usuario);
    }

    @Test
    @DisplayName("Deve buscar usuário por ID com sucesso")
    void deveBuscarUsuarioPorIdComSucesso() {
        when(usuarioSpringDataJPARepository.findById(usuarioId)).thenReturn(Optional.of(usuario));

        Usuario resultado = usuarioInfraRepository.buscaUsuarioAtravesId(usuarioId);

        assertNotNull(resultado);
        assertEquals(usuario, resultado);
        verify(usuarioSpringDataJPARepository, times(1)).findById(usuarioId);
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não encontrado por ID")
    void deveLancarExcecaoQuandoUsuarioNaoEncontradoPorId() {
        when(usuarioSpringDataJPARepository.findById(usuarioId)).thenReturn(Optional.empty());

        APIException ex = assertThrows(APIException.class,
                () -> usuarioInfraRepository.buscaUsuarioAtravesId(usuarioId));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusException());
        assertEquals("Usuario não encontrado", ex.getMessage());
        verify(usuarioSpringDataJPARepository, times(1)).findById(usuarioId);
    }

    @Test
    @DisplayName("Deve buscar usuário por CPF com sucesso")
    void deveBuscarUsuarioPorCpfComSucesso() {
        when(usuarioSpringDataJPARepository.findByCpf(cpfUsuario)).thenReturn(Optional.of(usuario));

        Usuario resultado = usuarioInfraRepository.buscaUsuarioPorCpf(cpfUsuario);

        assertNotNull(resultado);
        assertEquals(usuario, resultado);
        verify(usuarioSpringDataJPARepository, times(1)).findByCpf(cpfUsuario);
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não encontrado por CPF")
    void deveLancarExcecaoQuandoUsuarioNaoEncontradoPorCpf() {
        when(usuarioSpringDataJPARepository.findByCpf(cpfUsuario)).thenReturn(Optional.empty());

        APIException ex = assertThrows(APIException.class, () -> usuarioInfraRepository.buscaUsuarioPorCpf(cpfUsuario));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusException());
        assertEquals("Usuario não encontrado", ex.getMessage());
        verify(usuarioSpringDataJPARepository, times(1)).findByCpf(cpfUsuario);
    }

    @Test
    @DisplayName("Deve deletar usuário com sucesso")
    void deveDeletarUsuarioComSucesso() {
        usuarioInfraRepository.deletaUsuario(usuario);

        verify(usuarioSpringDataJPARepository, times(1)).delete(usuario);
    }

    @Test
    @DisplayName("Deve buscar usuário por email com sucesso")
    void deveBuscarUsuarioPorEmailComSucesso() {
        String email = "teste@email.com";
        when(usuarioSpringDataJPARepository.findByEmail(email)).thenReturn(Optional.of(usuario));

        Optional<Usuario> resultado = usuarioSpringDataJPARepository.findByEmail(email);

        assertNotNull(resultado);
        assertEquals(Optional.of(usuario), resultado);
        verify(usuarioSpringDataJPARepository, times(1)).findByEmail(email);
    }

    @Test
    @DisplayName("Deve retornar Optional vazio quando usuário não encontrado por email")
    void deveRetornarOptionalVazioQuandoUsuarioNaoEncontradoPorEmail() {
        String email = "naoexiste@email.com";
        when(usuarioSpringDataJPARepository.findByEmail(email)).thenReturn(Optional.empty());

        Optional<Usuario> resultado = usuarioSpringDataJPARepository.findByEmail(email);

        assertNotNull(resultado);
        assertEquals(Optional.empty(), resultado);
        verify(usuarioSpringDataJPARepository, times(1)).findByEmail(email);
    }
}
