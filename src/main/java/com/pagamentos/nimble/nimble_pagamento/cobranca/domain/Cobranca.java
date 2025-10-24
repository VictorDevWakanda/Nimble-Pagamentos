package com.pagamentos.nimble.nimble_pagamento.cobranca.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.pagamentos.nimble.nimble_pagamento.cobranca.application.api.CobrancaRequest;
import com.pagamentos.nimble.nimble_pagamento.usuario.domain.Usuario;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "cobranca")
public class Cobranca {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_cobranca", columnDefinition = "uuid")
    private UUID idCobranca;

    @NotNull(message = "Informe o Id do usuario originador")
    @Column(name = "id_usuario_originador", columnDefinition = "uuid", nullable = false)
    private UUID idUsuarioOriginador;

    @NotNull(message = "Informe o CPF do destinatário")
    @Pattern(regexp = "^[0-9]{11}$", message = "CPF do destinatário inválido")
    @Column(name = "cpf_destinatario", nullable = false)
    private String cpfDestinatario;

    @NotNull(message = "O valor da cobrança é obrigatório.")
    @DecimalMin(value = "0.01", message = "O valor da cobrança deve ser maior que zero.")
    @Digits(integer = 10, fraction = 2, message = "O valor deve ter até 10 dígitos e 2 casas decimais.")
    @Column(name = "valor_cobranca", precision = 10, scale = 2, nullable = false)
    private BigDecimal valorCobranca;

    @Column(name = "descricao", length = 500)
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusCobranca status;

    @Column(name = "data_expiracao")
    private LocalDateTime dataExpiracao;

    @Column(name = "data_pagamento")
    private LocalDateTime dataPagamento;

    @Column(name = "data_cancelamento")
    private LocalDateTime dataCancelamento;

    @Enumerated(EnumType.STRING)
    @Column(name = "forma_pagamento")
    private FormaPagamento formaPagamento;

    public Cobranca(UUID idUsuarioOriginador,
            CobrancaRequest request) {
        this.idUsuarioOriginador = idUsuarioOriginador;
        this.cpfDestinatario = request.cpfDestinatario();
        this.valorCobranca = request.valorCobranca();
        this.descricao = request.descricao();
        this.status = StatusCobranca.PENDENTE;
        this.dataExpiracao = LocalDateTime.now().plusDays(30); // Expira em 30 dias
    }

    public void cancelarComEstorno() {
        if (!this.estaPaga()) {
            throw new IllegalStateException("Estorno só pode ser feito em cobranças pagas.");
        }
        this.status = StatusCobranca.CANCELADA;
        this.dataCancelamento = LocalDateTime.now();
    }

    public boolean estaPendente() {
        return this.status == StatusCobranca.PENDENTE;
    }

    public boolean estaPaga() {
        return this.status == StatusCobranca.PAGA;
    }

    public boolean estaCancelada() {
        return this.status == StatusCobranca.CANCELADA;
    }

    public boolean pertenceAoOriginador(UUID usuarioId) {
        return this.idUsuarioOriginador.equals(usuarioId);
    }

    public boolean ehDestinatario(String cpfUsuario) {
        return this.cpfDestinatario.equals(cpfUsuario.replaceAll("[^0-9]", ""));
    }

    public void efetuarPagamento(Usuario pagador) {
        if (this.estaPaga()) {
            throw new IllegalStateException("Cobrança já foi paga.");
        }

        pagador.debitar(this.valorCobranca);
        this.status = StatusCobranca.PAGA;
        this.dataPagamento = LocalDateTime.now();
        this.formaPagamento = FormaPagamento.SALDO;
    }

    public void efetuarPagamentoComCartao() {
        if (!this.estaPendente()) {
            throw new IllegalStateException("Apenas cobranças pendentes podem ser pagas com cartão.");
        }
        this.status = StatusCobranca.PAGA;
        this.dataPagamento = LocalDateTime.now();
        this.formaPagamento = FormaPagamento.CARTAO;
    }

    public void marcarComoCancelada() {
        this.status = StatusCobranca.CANCELADA;
        this.dataCancelamento = LocalDateTime.now();
    }

    public boolean foiPagaComSaldo() {
        return this.formaPagamento == FormaPagamento.SALDO;
    }

    public boolean foiPagaComCartao() {
        return this.formaPagamento == FormaPagamento.CARTAO;
    }

}
