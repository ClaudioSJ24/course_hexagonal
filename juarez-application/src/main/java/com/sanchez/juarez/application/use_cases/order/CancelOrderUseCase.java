package com.sanchez.juarez.application.use_cases.order;

import com.juarez.domain.entities.order.OrderRoot;
import com.juarez.domain.ports.repositories.OrderRepositoryPort;
import com.sanchez.juarez.application.commands.helpers.CommandHelper;
import com.sanchez.juarez.application.commands.order.CancelOrderCommand;
import com.sanchez.juarez.application.exceptions.CommandException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CancelOrderUseCase {

    private static final Logger log = LoggerFactory.getLogger(CancelOrderUseCase.class);
    private final OrderRepositoryPort orderRepositoryPort;
    private final CommandHelper commandHelper;


    public CancelOrderUseCase(OrderRepositoryPort orderRepositoryPort, CommandHelper commandHelper) {
        this.orderRepositoryPort = orderRepositoryPort;
        this.commandHelper = commandHelper;


    }

    public void execute(CancelOrderCommand command){

        try {

            log.info("Cancel order {}", command.orderId());
            OrderRoot orderRoot = this.commandHelper.findOrderById(command.orderId());

            orderRoot.cancel(command.reason());

            this.orderRepositoryPort.save(orderRoot);
            log.info("Order {} cancelled", command.orderId());
        }catch (Exception e) {
            log.error("Error on cancel order", e);
            throw new CommandException("Error on cancel order");
        }
    }
}
