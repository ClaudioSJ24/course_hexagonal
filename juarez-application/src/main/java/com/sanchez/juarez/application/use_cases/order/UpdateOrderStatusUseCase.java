package com.sanchez.juarez.application.use_cases.order;

import com.juarez.domain.entities.order.OrderRoot;
import com.juarez.domain.ports.repositories.OrderRepositoryPort;
import com.sanchez.juarez.application.commands.helpers.CommandHelper;
import com.sanchez.juarez.application.commands.order.UpdateOrderStatusCommand;
import com.sanchez.juarez.application.exceptions.CommandException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * * JIRA TICKET: ERP-6734
 * Business Rules:
 * - Order must exist
 * - Status transition must be valid (enforced by domain)
 * Valid Transitions:
 * - PENDING → CONFIRMED
 * - CONFIRMED → SHIPPED
 * - SHIPPED → DELIVERED
 * 1. Find order by ID
 * 2. Update status (domain validates transition)
 * 3. Persist order
 *
 */

@Service
@Transactional
public class UpdateOrderStatusUseCase {

    private static final Logger log = LoggerFactory.getLogger(UpdateOrderStatusUseCase.class);
    private final OrderRepositoryPort orderRepositoryPort;
    private final CommandHelper commandHelper;

    public UpdateOrderStatusUseCase(OrderRepositoryPort orderRepositoryPort, CommandHelper commandHelper) {
        this.orderRepositoryPort = orderRepositoryPort;
        this.commandHelper = commandHelper;
    }

    public String execute(UpdateOrderStatusCommand command){

        try {

            OrderRoot orderRoot = this.commandHelper.findOrderById(command.orderId());
            log.info("Current order current status: {}", OrderRoot.getStatus());

            this.updateStatus(orderRoot, command.newStatus());

            OrderRoot orderSaved = this.orderRepositoryPort.save(orderRoot);
            log.info("Order saved current status: {}", orderSaved.getStatus());

            return OrderRoot.getStatus().toString();
        }catch (IllegalStateException ise) {
            log.error("Error updating order status", ise);
            throw new CommandException("Error updating order status");
        } catch (Exception e) {
            log.error("Error updating order status", e);
            throw new CommandException("Unexpected error updating order status");
        }


    }

    private void updateStatus(OrderRoot orderRoot, @NotBlank(message = "New status cannot be null or blank") @Pattern(
            regexp = "^(CONFIRMED|SHIPPED|DELIVERED)$",
            message = "Status must be one of: CONFIRMED, SHIPPED, DELIVERED. Use CancelOrderCommand to cancel an order."
    ) String status) {

        switch (status.toUpperCase()){

            case "CONFIRMED" -> orderRoot.confirm();
            case "SHIPPED" -> orderRoot.ship();
            case "DELIVERED" -> orderRoot.deliver();
            default -> throw new CommandException("Unexpected value: "+status);

        }
    }


}
