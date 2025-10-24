package com.pagamentos.nimble.nimble_pagamento.cobranca.application.api;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CobrancaRequest(

                @NotBlank(message = "Informe o CPF do destinatário") String cpfDestinatario,

                @DecimalMax(value = "100000.00", message = "Valor máximo da cobrança é R$100.000,00") BigDecimal valorCobranca,

                @Size(max = 500, message = "Limite de caracteres excedido") String descricao) {
}
