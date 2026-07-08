package com.juarez.domain.entities.views;

public record ItemsView(
        String code,
        String value,
        String description,
        Integer displayOrder
) {
}
