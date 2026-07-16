package com.sanchez.juarez.application.use_cases.product;

import com.juarez.domain.entities.product.ProductId;
import com.juarez.domain.entities.product.ProductRoot;
import com.juarez.domain.ports.repositories.ProductRepositoryPort;
import com.sanchez.juarez.application.commands.product.DesactivateProductCommand;
import com.sanchez.juarez.application.exceptions.CommandException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DesactivateProductUseCase {

    private static final Logger log = LoggerFactory.getLogger(DesactivateProductUseCase.class);
    private final ProductRepositoryPort productRepository;

    public DesactivateProductUseCase(ProductRepositoryPort productRepository) {
        this.productRepository = productRepository;
    }

    public void execute(DesactivateProductCommand command) {

        log.info("Deactivating product: {}", command.productId());

        try {
            // 1. Find product
            ProductRoot product = findProductById(command.productId());

            log.debug("Current status: active={}", product.isActive());

            // 2. Deactivate product
            product.desactivate();

            log.debug("Product deactivated in domain");

            // 3. Persist changes
            productRepository.save(product);

            log.info("Product deactivation persisted");

        } catch (IllegalStateException ise) {
            log.error("Product already deactivated", ise);
            throw new CommandException("Product is already deactivated");
        } catch (Exception e) {
            log.error("Unexpected error deactivating product", e);
            throw new CommandException("Failed to deactivate product: " + e.getMessage());
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
