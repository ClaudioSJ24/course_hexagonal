package com.juarez.domain.entities.order.events;

import com.juarez.domain.common.DomainEvent;
import com.juarez.domain.entities.order.OrderId;
import com.juarez.domain.shared.CustomerId;
import com.juarez.domain.shared.Money;

import java.time.Instant;

/**
 * Emitted when a new order is created.
 *
 * @param orderId      the order identifier
 * @param customerId   the customer identifier
 * @param customerName the customer name
 * @param totalAmount  the total order amount
 * @param timestamp    the event timestamp
 */
public record OrderCreated(
        OrderId orderId,
        CustomerId customerId,
        String customerName,
        Money totalAmount,
        Instant timestamp
) implements DomainEvent {
}
