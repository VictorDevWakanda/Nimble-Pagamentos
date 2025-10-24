package com.pagamentos.nimble.nimble_pagamento.usuario.domain;

import java.math.BigDecimal;
import java.util.UUID;

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
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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

    @NotBlank(message = "CPF é obrigatório")
    @Pattern(regexp = "^[0-9]{11}$", message = "CPF inválido")
    @Column(name = "cpf", unique = true)
    private String cpf;

    @NotBlank
    @Email(message = "Email inválido")
    @Column(name = "email", unique = true)
    private String email;

    @NotBlank
    @Column(name = "senha_hash")
    private String senhaHash;

    @NotNull(message = "Informe o saldo da conta")
    @Column(name = "saldo", precision = 10, scale = 2, nullable = false)
    private BigDecimal saldo = BigDecimal.ZERO;

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

    public boolean destinadaPara(String cpf) {
        return this.cpf.equals(cpf.replaceAll("[^0-9]", ""));
    }

    public String getCpfLimpo() {
        return this.cpf.replaceAll("[^0-9]", "");
    }

    public void debitar(BigDecimal valor) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor do débito deve ser maior que zero.");
        }

        if (this.saldo.compareTo(valor) < 0) {
            throw new IllegalStateException("Saldo insuficiente para realizar o débito.");
        }

        this.saldo = this.saldo.subtract(valor);
    }

    public void creditar(BigDecimal valor) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor do crédito deve ser maior que zero.");
        }
        this.saldo = this.saldo.add(valor);
    }

}
