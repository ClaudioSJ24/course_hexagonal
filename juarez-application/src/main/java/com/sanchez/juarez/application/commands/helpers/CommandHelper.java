package com.sanchez.juarez.application.commands.helpers;

import com.juarez.domain.entities.order.OrderId;
import com.juarez.domain.entities.order.OrderRoot;
import com.juarez.domain.ports.repositories.OrderRepositoryPort;
import com.sanchez.juarez.application.exceptions.CommandException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component

public class CommandHelper {

    private static final Logger log = LoggerFactory.getLogger(CommandHelper.class);
    private final OrderRepositoryPort orderRepositoryPort;


    public CommandHelper(OrderRepositoryPort orderRepositoryPort) {
        this.orderRepositoryPort = orderRepositoryPort;
    }

    public OrderRoot findOrderById(String orderId){

        log.info("Finding order by ID: {}", orderId);

        return this.orderRepositoryPort.findById(OrderId.of(UUID.fromString(orderId)))
                .orElseThrow(() -> new CommandException("Order not found"));
    }
}
