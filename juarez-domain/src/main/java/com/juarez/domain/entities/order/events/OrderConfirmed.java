package com.juarez.domain.entities.order.events;

import com.juarez.domain.common.DomainEvent;
import com.juarez.domain.entities.order.OrderId;

import java.time.Instant;

/**
 * Emitted when order transitions PENDING -> CONFIRMED.
 * TRIGGERS stock decrement.
 *
 * @param orderId   the order identifier
 * @param timestamp the event timestamp
 */
public record OrderConfirmed(
        OrderId orderId,
        Instant timestamp
) implements DomainEvent {
}
