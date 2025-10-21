package com.pagamentos.nimble.nimble_pagamento.usuario.domain;

import java.util.UUID;

import org.hibernate.validator.constraints.br.CPF;

import com.pagamentos.nimble.nimble_pagamento.usuario.application.api.UsuarioAlteracaoRequest;
import com.pagamentos.nimble.nimble_pagamento.usuario.application.api.UsuarioRequest;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_usuario", columnDefinition = "uuid")
    private UUID idUsuario;

    @NotBlank(message = "Nome é obrigatório")
    @Column(name = "nome")
    private String nome;

    @NotBlank
    @CPF(message = "CPF inválido")
    @Column(name = "cpf", unique = true)
    private String cpf;

    @NotBlank
    @Email(message = "Email inválido")
    @Column(name = "email", unique = true)
    private String email;

    @NotBlank
    @Column(name = "senha_hash")
    private String senhaHash;

    public Usuario(UsuarioRequest usuarioRequest, String senhaHash) {
        this.nome = usuarioRequest.getNome();
        this.cpf = usuarioRequest.getCpf();
        this.email = usuarioRequest.getEmail();
        this.senhaHash = senhaHash;
    }

    public void altera(UsuarioAlteracaoRequest usuarioAlteracaoRequest, String senhaHash) {
        this.nome = usuarioAlteracaoRequest.getNome();
        this.email = usuarioAlteracaoRequest.getEmail();
        this.senhaHash = senhaHash;
    }

}
