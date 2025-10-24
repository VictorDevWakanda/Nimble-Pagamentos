package com.pagamentos.nimble.nimble_pagamento.docs.swagger;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.http.MediaType;

import com.pagamentos.nimble.nimble_pagamento.cobranca.application.api.CobrancaResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

public @interface CobrancaAPIDocs {
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "Cadastro de Cobrança", description = "Cadastra uma nova cobrança informando o CPF do destinatário, o valor, a descrição e a data de expiração.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cobrança criada com sucesso.", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CobrancaResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(value = "{ \"message\": \"Dados inválidos\" }")))
    })
    public @interface CadastraCobranca {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "Consulta Cobrança por ID", description = "Retorna os detalhes da cobrança pelo ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cobrança encontrada.", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CobrancaResponse.class))),
            @ApiResponse(responseCode = "404", description = "Cobrança não encontrada", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(value = "{ \"message\": \"Cobrança não encontrada\" }")))
    })
    public @interface ConsultaCobrancaPorId {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "Lista cobranças pendentes como do usuário", description = "Retorna as cobranças pendentes do usuário.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cobranças encontradas.", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CobrancaResponse.class))),
            @ApiResponse(responseCode = "404", description = "Nenhuma cobrança encontrada.", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(value = "{ \"message\": \"Nenhuma cobrança encontrada\" }")))
    })
    public @interface ListaCobrancaDoOriginador {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "Lista cobranças pendentes do destinatário", description = "Retorna as cobranças pendentes do destinatário.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cobranças encontradas.", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CobrancaResponse.class))),
            @ApiResponse(responseCode = "404", description = "Nenhuma cobrança encontrada.", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(value = "{ \"message\": \"Nenhuma cobrança encontrada\" }")))
    })
    public @interface ListaCobrancaDoDestinatario {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "Efetua pagamento", description = "Efetua o pagamento da cobrança.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pagamento efetuado com sucesso.", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CobrancaResponse.class))),
            @ApiResponse(responseCode = "404", description = "Cobrança não encontrada", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(value = "{ \"message\": \"Cobrança não encontrada\" }")))
    })
    public @interface EfetuaPagamento {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "Efetua pagamento com cartão", description = "Efetua o pagamento da cobrança com cartão.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pagamento efetuado com sucesso.", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CobrancaResponse.class))),
            @ApiResponse(responseCode = "404", description = "Cobrança não encontrada", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(value = "{ \"message\": \"Cobrança não encontrada\" }")))
    })
    public @interface EfetuaPagamentoComCartao {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "Cancela cobrança", description = "Cancela a cobrança do solicitante.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cobrança cancelada com sucesso.", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CobrancaResponse.class))),
            @ApiResponse(responseCode = "404", description = "Cobrança não encontrada", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(value = "{ \"message\": \"Cobrança não encontrada\" }")))
    })
    public @interface CancelaCobranca {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "Realiza depósito", description = "Realiza o depósito do usuário.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Depósito realizado com sucesso.", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CobrancaResponse.class))),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(value = "{ \"message\": \"Usuário não encontrado\" }")))
    })
    public @interface RealizaDeposito {
    }

    // TODO Validar e talvez corrigir!!!

}
