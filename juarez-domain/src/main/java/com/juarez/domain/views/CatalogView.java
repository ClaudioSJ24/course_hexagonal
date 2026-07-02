package com.juarez.domain.views;

import com.sanchez.juarez.common.enums.CatalogType;

import java.time.Instant;
import java.util.List;

/**
 * Read model representation of a Catalog.
 * Used for queries (CQRS read side).
 * This is a simplified view optimized for display.
 */
public record CatalogView(
        boolean active,
        String name,
        String description,
        CatalogType type,
        Instant createdAt,
        Instant updatedAt,
        List<ItemsView> items
) {
}
