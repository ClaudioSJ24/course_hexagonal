package com.sanchez.juarez.application.use_cases.product;

import com.juarez.domain.entities.product.ProductId;
import com.juarez.domain.entities.product.ProductRoot;
import com.juarez.domain.ports.repositories.ProductRepositoryPort;
import com.sanchez.juarez.application.commands.product.UpdateStockCommand;
import com.sanchez.juarez.application.exceptions.CommandException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class UpdateStockUseCase {

    private static final Logger log = LoggerFactory.getLogger(UpdateStockUseCase.class);
    private final ProductRepositoryPort productRepository;

    public UpdateStockUseCase(ProductRepositoryPort productRepository) {
        this.productRepository = productRepository;
    }

    public void execute(UpdateStockCommand command) {

        log.info("Updating stock for product: {} by {} units (reason: {})",
                command.productId(),
                command.quantity(),
                command.reason());

        try {
            // 1. Find product
            ProductRoot product = findProductById(command.productId());

            log.debug("Current stock: {}", product.getStock().value());

            // 2. Update stock based on operation
            if (command.isIncrement()) {
                product.incrementStock(command.absoluteQuantity(), command.reason());
                log.debug("Stock incremented by {} units", command.absoluteQuantity());
            } else if (command.isDecrement()) {
                product.decrementStock(command.absoluteQuantity(), command.reason());
                log.debug("Stock decremented by {} units", command.absoluteQuantity());
            }

            log.debug("New stock: {}", product.getStock().value());

            // 3. Persist changes
            productRepository.save(product);

            log.info("Stock update persisted. New stock: {}", product.getStock().value());

        } catch (IllegalArgumentException iae) {
            log.error("Invalid stock update", iae);
            throw new CommandException("Error updating stock: " + iae.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error updating stock", e);
            throw new CommandException("Failed to update stock: " + e.getMessage());
        }
    }

    private ProductRoot findProductById(String productId) {
        log.debug("Finding product by ID: {}", productId);

        ProductId productIdVO = ProductId.of(UUID.fromString(productId));

        return productRepository.findAllById(productIdVO)
                .orElseThrow(() -> {
                    log.warn("Product not found: {}", productId);
                    return new CommandException("Product not found with ID: " + productId);
                });
    }
}
