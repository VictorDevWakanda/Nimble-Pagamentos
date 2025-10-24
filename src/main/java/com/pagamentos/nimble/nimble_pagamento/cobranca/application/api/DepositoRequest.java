package com.pagamentos.nimble.nimble_pagamento.cobranca.application.api;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

@Value
public class DepositoRequest {

    @NotNull(message = "Valor do depósito é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor mínimo para depósito é R$0,01")
    @DecimalMax(value = "100000.00", message = "Valor máximo da cobrança é R$100.000,00")
    @Digits(integer = 10, fraction = 2, message = "Valor deve ter até 10 dígitos e 2 casas decimais")
    private BigDecimal valor;
}
