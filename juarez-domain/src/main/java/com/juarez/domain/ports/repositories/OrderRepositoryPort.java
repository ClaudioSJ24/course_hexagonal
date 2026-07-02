package com.juarez.domain.ports.repositories;

import com.juarez.domain.entities.order.OrderId;
import com.juarez.domain.entities.order.OrderNumber;
import com.juarez.domain.entities.order.OrderRoot;
import com.juarez.domain.shared.CustomerId;

import java.util.List;
import java.util.Optional;
/**
 *  Port for storage o consult Orders
 */
public interface OrderRepositoryPort {

    OrderRoot save(OrderRoot order);
    Optional<OrderRoot> findById(OrderId id);
    Optional<OrderRoot> findAllByOrderNumber(OrderNumber orderNumber);
    List<OrderRoot> findCustomerId(CustomerId customerId);
    void delete(OrderRoot order);
}
