package com.sanchez.juarez.application.use_cases.order;

import com.juarez.domain.entities.order.Customer;
import com.juarez.domain.entities.order.OrderItem;
import com.juarez.domain.entities.order.OrderNumber;
import com.juarez.domain.entities.order.OrderRoot;
import com.juarez.domain.entities.product.ProductId;
import com.juarez.domain.entities.product.ProductRoot;
import com.juarez.domain.ports.repositories.OrderRepositoryPort;
import com.juarez.domain.ports.repositories.ProductRepositoryPort;
import com.juarez.domain.ports.services.CustomerProviderServicePort;
import com.juarez.domain.ports.services.OrderConfirmEmailServicePort;
import com.juarez.domain.shared.CustomerId;
import com.juarez.domain.shared.Email;
import com.juarez.domain.shared.Quantity;
import com.sanchez.juarez.application.commands.order.CreateOrderCommand;
import com.sanchez.juarez.application.exceptions.CommandException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;


/**
 * JIRA TICKET: ERP-6734
 * Business Rules:
 * - Customer must exist in external system (JSONPlaceholder)
 * - All products must exist and be active
 * - All products must have sufficient stock
 * - Order number is generated automatically
 * Flow:
 * 1. Validate customer exists
 * 2. Validate products exist
 * 3. Create order items from products (snapshot prices)
 * 4. Create order aggregate (domain generates ID)
 * 5. Persist order (write model - PostgreSQL)
 * 6. Publish domain events (for MongoDB sync, email notifications)
 */
@Service
@Transactional
public class CreateOrderUseCase {

    private static final Logger log = LoggerFactory.getLogger(CreateOrderUseCase.class);
    private final OrderRepositoryPort orderRepositoryPort;
    private final ProductRepositoryPort productRepositoryPort;
    private final CustomerProviderServicePort customerProviderServicePort;
    private final OrderConfirmEmailServicePort orderConfirmEmailServicePort;


    public CreateOrderUseCase(OrderRepositoryPort orderRepositoryPort, ProductRepositoryPort productRepositoryPort, CustomerProviderServicePort customerProviderServicePort, OrderConfirmEmailServicePort orderConfirmEmailServicePort) {
        this.orderRepositoryPort = orderRepositoryPort;
        this.productRepositoryPort = productRepositoryPort;
        this.customerProviderServicePort = customerProviderServicePort;
        this.orderConfirmEmailServicePort = orderConfirmEmailServicePort;
    }

    public String execute(CreateOrderCommand command){

        log.info("Creating order {}", command);

        try {

            Customer customer = this.validateAndGet(command.customerId());

            List<OrderItem> orderItems = this.createOrderItems(command.items());

            OrderNumber orderNumber = this.generateOrderNumber();

            OrderRoot orderRoot =  OrderRoot.create(
                    orderNumber,
                    customer,
                    orderItems,
                    command.createdBy()
            );

            OrderRoot savedOrder = this.orderRepositoryPort.save(orderRoot);

            log.info("Saved order with id {}", savedOrder.getId());

            this.sendEmail(orderRoot, customer);
            return orderRoot.getId().toString();

        } catch (IllegalArgumentException iae) {
            log.error("Invalid data", iae);
            throw new CommandException("Error on create order msg: " + iae.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error", e);
            throw new CommandException(e.getMessage());
        }

    }

    private void sendEmail(OrderRoot orderRoot, Customer customer) {

        try {
            log.info("Sending mail: {}", customer.customerName() + "@gamil.com");
            final var mail = Email.of("cocoloco@gmail.com");

            this.orderConfirmEmailServicePort.sendMail(
                    mail,
                    orderRoot.getId(),
                    orderRoot.getOrderNumber().value(),
                    orderRoot.getTotalAmount(),
                    customer.customerName(),
                    orderRoot.getItems().size()
            );
        }catch (Exception e) {
            log.error("Error sending mail", e);
            throw new CommandException("Error sending mail: " + e.getMessage());
        }
    }

    private OrderNumber generateOrderNumber() {

        int sequence = (int) (System.currentTimeMillis() % 1000);
        return OrderNumber.generate(sequence);
    }

    private List<OrderItem> createOrderItems(
            @NotNull(message = "Order items cannot be null")
            @NotEmpty(message = "Order must have at least one item")
            @Valid List<CreateOrderCommand.OrderItemRequest> commandItems ) {

        log.info("Creating order items");

        return commandItems
                .stream()
                .map(this::toOrderItem)
                .toList();
    }

    private OrderItem toOrderItem(CreateOrderCommand.OrderItemRequest commandItem) {

        ProductRoot productRoot = this.productRepositoryPort
                .findAllById(ProductId.of(UUID.fromString(commandItem.productId())))
                .orElseThrow( () -> new CommandException("Product not found"));

        Quantity quantity = Quantity.of(commandItem.quantity());

        return OrderItem.from(productRoot, quantity);
    }

    private Customer validateAndGet(
            @NotNull(message = "Customer ID cannot be null")
            @Min(value = 1, message = "Customer ID must be greater than 0")
            Long customerId) {
        log.info("Validating customer id {}", customerId);

        var customerInfo = this.customerProviderServicePort.findById(customerId)
                .orElseThrow(() -> new CommandException("Customer not found is "+customerId));

        log.info("Customer validated with name {}", customerInfo.name());

        return Customer.of(CustomerId.of(customerId), customerInfo.name());

    }

    private void publishDomanEvent(OrderRoot order) {
        var events = order.getDomainEvents();
        log.info("Publishing doman events: {}", events);
        events.forEach(event -> {
            log.debug("Try to publish event: {}", event);
            // TODO: send event on queue
        });

        order.clearDomainEvents();
        log.info("Events published SUCCESSFULLY");
    }
}
