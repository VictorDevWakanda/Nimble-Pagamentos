package com.pagamentos.nimble.nimble_pagamento.cobranca.application.api;

import lombok.Value;

@Value
public class ValidadorResponse {
    private String status;
    private Data data;

    @Value
    public static class Data {
        private boolean authorized;

        public boolean isAuthorized() {
            return authorized;
        }
    }

}
