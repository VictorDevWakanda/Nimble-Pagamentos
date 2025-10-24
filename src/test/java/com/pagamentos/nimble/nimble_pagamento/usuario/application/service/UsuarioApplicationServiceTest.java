package com.pagamentos.nimble.nimble_pagamento.usuario.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.pagamentos.nimble.nimble_pagamento.usuario.application.api.UsuarioAlteracaoRequest;
import com.pagamentos.nimble.nimble_pagamento.usuario.application.api.UsuarioDetalhadoResponse;
import com.pagamentos.nimble.nimble_pagamento.usuario.application.api.UsuarioRequest;
import com.pagamentos.nimble.nimble_pagamento.usuario.application.api.UsuarioResponse;
import com.pagamentos.nimble.nimble_pagamento.usuario.application.repository.UsuarioRepository;
import com.pagamentos.nimble.nimble_pagamento.usuario.dataHelper.UsuarioDataHelper;
import com.pagamentos.nimble.nimble_pagamento.usuario.domain.Usuario;
import com.pagamentos.nimble.nimble_pagamento.usuario.domain.service.SenhaHashService;

@ExtendWith(MockitoExtension.class)
public class UsuarioApplicationServiceTest {

    @InjectMocks
    private UsuarioApplicationService usuarioApplicationService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private SenhaHashService senhaHashService;

    private UUID idUsuario;
    private UsuarioRequest usuarioRequestValido;
    private UsuarioAlteracaoRequest usuarioAlteracaoRequestValido;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        idUsuario = UUID.randomUUID();
        usuarioRequestValido = UsuarioDataHelper.criarUsuarioRequestValido();
        usuarioAlteracaoRequestValido = UsuarioDataHelper.criarUsuarioAlteracaoRequestValido();
        usuario = UsuarioDataHelper.criarUsuario();
    }

    @Test
    @DisplayName("Deve criar usuário com sucesso")
    void criaUsuarioComSucesso() {
        when(senhaHashService.gerarHash(usuarioRequestValido.getSenha())).thenReturn("hashedPassword123");
        when(usuarioRepository.salvaUsuario(any(Usuario.class))).thenReturn(usuario);

        UsuarioResponse response = usuarioApplicationService.criaUsuario(usuarioRequestValido);

        assertNotNull(response);
        assertEquals(usuario.getIdUsuario(), response.getIdUsuario());

        verify(senhaHashService).gerarHash(usuarioRequestValido.getSenha());
        verify(usuarioRepository).salvaUsuario(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve buscar usuário por ID com sucesso")
    void buscaUsuarioPorIdComSucesso() {
        when(usuarioRepository.buscaUsuarioAtravesId(idUsuario)).thenReturn(usuario);

        UsuarioDetalhadoResponse response = usuarioApplicationService.buscaUsuarioAtravesId(idUsuario);

        assertNotNull(response);
        assertEquals(usuario.getIdUsuario(), response.getIdUsuario());
        assertEquals(usuario.getNome(), response.getNome());
        assertEquals(usuario.getCpf(), response.getCpf());
        assertEquals(usuario.getEmail(), response.getEmail());

        verify(usuarioRepository).buscaUsuarioAtravesId(idUsuario);
    }

    @Test
    @DisplayName("Deve alterar usuário com sucesso")
    void alteraUsuarioComSucesso() {
        when(usuarioRepository.buscaUsuarioAtravesId(idUsuario)).thenReturn(usuario);
        when(senhaHashService.gerarHash(usuarioAlteracaoRequestValido.getSenha())).thenReturn("novaHashedPassword");
        when(usuarioRepository.salvaUsuario(usuario)).thenReturn(usuario);

        usuarioApplicationService.patchAlteraUsuario(idUsuario, usuarioAlteracaoRequestValido);

        verify(usuarioRepository).buscaUsuarioAtravesId(idUsuario);
        verify(senhaHashService).gerarHash(usuarioAlteracaoRequestValido.getSenha());
        verify(usuarioRepository).salvaUsuario(usuario);
    }

    @Test
    @DisplayName("Deve deletar usuário com sucesso")
    void deletaUsuarioComSucesso() {
        when(usuarioRepository.buscaUsuarioAtravesId(idUsuario)).thenReturn(usuario);

        usuarioApplicationService.deletaUsuarioAtravesId(idUsuario);

        verify(usuarioRepository).buscaUsuarioAtravesId(idUsuario);
        verify(usuarioRepository).deletaUsuario(usuario);
    }
}
