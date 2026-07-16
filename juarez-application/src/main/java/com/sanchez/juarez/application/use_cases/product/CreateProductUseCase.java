package com.sanchez.juarez.application.use_cases.product;

import com.juarez.domain.entities.product.*;
import com.juarez.domain.ports.repositories.ProductRepositoryPort;
import com.juarez.domain.ports.services.ImageStorageServicePort;
import com.juarez.domain.shared.Money;
import com.sanchez.juarez.application.commands.product.CreateProductCommand;
import com.sanchez.juarez.application.exceptions.CommandException;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Currency;

/**
 * Use Case: Create a new product.
 * JIRA TICKET: ERP-6735
 * 1. Validate SKU uniqueness
 * 2. Upload image to S3 (if provided)
 * 3. Create ProductRoot aggregate
 * 4. Persist to PostgreSQL
 * 5. Publish ProductCreated event (CQRS sync to MongoDB)
 */

@Service
@Transactional
public class CreateProductUseCase {

    private static final Logger log = LoggerFactory.getLogger(CreateProductUseCase.class);
    private final ProductRepositoryPort productRepositoryPort;
    private final ImageStorageServicePort imageStorageServicePort;

    public CreateProductUseCase(ProductRepositoryPort productRepositoryPort, ImageStorageServicePort imageStorageServicePort) {
        this.productRepositoryPort = productRepositoryPort;
        this.imageStorageServicePort = imageStorageServicePort;
    }


    public String execute(CreateProductCommand createProductCommand){

        log.info("Creating product with SKU: {}", createProductCommand.sku());

        try {

            // 1. Validate SKU uniqueness
            validateSkuUniqueness(createProductCommand.sku());

            // 2. Upload image (if provided)


            // 3. Create value objects
            SKU sku = SKU.of(createProductCommand.sku());
            ProductName name = ProductName.of(createProductCommand.name());
            Money price = Money.of(createProductCommand.price(), Currency.getInstance(createProductCommand.currency()));
            Stock stock = Stock.of(createProductCommand.stock());
            CategoryReference categoryReference = CategoryReference.of(createProductCommand.categoryId());

            ProductImage image = uploadImg(createProductCommand);


            // 4. Create product aggregate
            ProductRoot product = ProductRoot.create(
                    sku,
                    name,
                    createProductCommand.description(),
                    price,
                    stock,
                    categoryReference,
                    image,
                    createProductCommand.createdBy()
            );

            log.debug("Product created in domain with ID: {}", product.getId().value());

            // 5. Persist product
            ProductRoot savedProduct = this.productRepositoryPort.save(product);

            log.info("Product persisted with ID: {}", savedProduct.getId().value());

            // TODO: Handle domain events - Sync to MongoDB

            return  savedProduct.getId().value().toString();

        } catch (IllegalArgumentException iae) {
            log.error("Invalid data for product creation");
            throw new CommandException("Error creating product: " + iae.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error creating product", e);
            throw new CommandException("Failed to create product: " + e.getMessage());
        }

    }

    private ProductImage uploadImg(CreateProductCommand command) {

        if (!command.hasImage()) {
            log.info("Product image is empty");
            return null;
        }

        log.info("Uploading image with SKU: {}", command.sku());

        try {
            return this.imageStorageServicePort.upload(
                    command.imageName(),
                    command.imageData()
            );
        } catch (Exception e) {
            log.error("Unexpected error uploading image with SKU", e);
            throw new CommandException("Error uploading image with SKU: " + e.getMessage());
        }
    }

    private void validateSkuUniqueness(@NotBlank(message = "SKU cannot be null or blank") String sku) {

        log.debug("Validating SKU uniqueness: {}", sku);

        if (this.productRepositoryPort.findBySku(sku).isPresent()) {

            log.warn("SKU already exists: {}", sku);
            throw new CommandException("Product with SKU '" + sku + "' already exists");

        }
    }

}
