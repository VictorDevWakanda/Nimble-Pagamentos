package com.pagamentos.nimble.nimble_pagamento.cobranca.application.api;

import java.util.List;

import org.springframework.data.domain.Page;

import lombok.Value;

@Value
public class PageResponse<T> {
    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;

    public PageResponse(Page<T> page) {
        this.content = page.getContent();
        this.page = page.getNumber();
        this.size = page.getSize();
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
    }
}
