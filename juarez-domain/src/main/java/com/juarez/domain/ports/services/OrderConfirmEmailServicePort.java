package com.juarez.domain.ports.services;

import com.juarez.domain.entities.order.OrderId;
import com.juarez.domain.shared.Email;
import com.juarez.domain.shared.Money;

/**
 *  Port for email service in order created
 */
public interface OrderConfirmEmailServicePort {

    void sendMail(
            Email email,
            OrderId orderId,
            String orderNumber,
            Money money,
            String customerName,
            Integer itemsCount
    );
}
