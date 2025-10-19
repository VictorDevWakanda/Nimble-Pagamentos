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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
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

    @CPF
    @NotBlank(message = "CPF é obrigatório")
    @Column(name = "cpf", unique = true)
    private String cpf;

    @NotBlank(message = "Email é obrigatório")
    @Column(name = "email", unique = true)
    @Email(message = "Email inválido")
    private String email;

    //TODO Fazer logica encode para senha
    @NotBlank(message = "Insira uma senha")
    @Column(name = "senha_hash")
    private String senha;

    public Usuario(UsuarioRequest usuarioRequest) {
        this.nome = usuarioRequest.getNome();
        this.cpf = usuarioRequest.getCpf();
        this.email = usuarioRequest.getEmail();
        this.senha = usuarioRequest.getSenha();
    }

    public void altera(UsuarioAlteracaoRequest usuarioAlteracaoRequest) {
        this.nome = usuarioAlteracaoRequest.getNome();
        this.email = usuarioAlteracaoRequest.getEmail();
        this.senha = usuarioAlteracaoRequest.getSenha();
    }

}
