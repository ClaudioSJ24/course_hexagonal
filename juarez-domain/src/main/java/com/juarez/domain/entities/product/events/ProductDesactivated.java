package com.juarez.domain.entities.product.events;

import com.juarez.domain.common.DomainEvent;
import com.juarez.domain.entities.product.ProductId;

import java.time.Instant;

/**
 * Emitted when product is deactivated.
 * TRIGGERS sync to MongoDB.
 *
 * @param productId the product identifier
 * @param timestamp the event timestamp
 */
public record ProductDesactivated(
        ProductId productId,
        Instant timestamp
) implements DomainEvent {
}
