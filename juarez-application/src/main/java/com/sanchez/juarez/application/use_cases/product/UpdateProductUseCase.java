package com.sanchez.juarez.application.use_cases.product;

import com.juarez.domain.entities.product.*;
import com.juarez.domain.ports.repositories.ProductRepositoryPort;
import com.juarez.domain.ports.services.ImageStorageServicePort;
import com.juarez.domain.shared.Money;
import com.sanchez.juarez.application.commands.product.UpdateProductCommand;
import com.sanchez.juarez.application.exceptions.CommandException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Currency;
import java.util.UUID;

@Service
@Transactional
public class UpdateProductUseCase {

    private static final Logger log = LoggerFactory.getLogger(UpdateProductUseCase.class);
    private final ProductRepositoryPort productRepository;
    private final ImageStorageServicePort imageStorageService;

    public UpdateProductUseCase(ProductRepositoryPort productRepository, ImageStorageServicePort imageStorageService) {
        this.productRepository = productRepository;
        this.imageStorageService = imageStorageService;
    }

    public void execute(UpdateProductCommand command) {

        log.info("Updating product: {}", command.productId());

        try {
            // 1. Find product
            ProductRoot product = findProductById(command.productId());

            log.debug("Current product: SKU={}, Name={}",
                    product.getSku().value(),
                    product.getName().value());

            // 2. Handle image update (if provided)
            ProductImage oldImage = product.getImage();
            ProductImage newImage = updateImage(command, oldImage);

            // 3. Build updated values
            ProductName name = command.shouldUpdateName()
                    ? ProductName.of(command.name())
                    : product.getName();

            String description = command.description() != null
                    ? command.description()
                    : product.getDescription();

            Money price = command.shouldUpdatePrice()
                    ? Money.of(command.price(), Currency.getInstance(product.getPrice().currency().getCurrencyCode()))
                    : product.getPrice();

            CategoryReference category = command.shouldUpdateCategory()
                    ? CategoryReference.of(command.categoryId())
                    : product.getCategory();

            ProductImage finalImage = newImage != null ? newImage : product.getImage();

            // 4. Update product
            product.update(name, description, price, category, finalImage);

            log.debug("Product updated in domain");

            // 5. Persist changes
            productRepository.save(product);

            log.info("Product update persisted");

        } catch (IllegalArgumentException iae) {
            log.error("Invalid data for product update", iae);
            throw new CommandException("Error updating product: " + iae.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error updating product", e);
            throw new CommandException("Failed to update product: " + e.getMessage());
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

    private ProductImage updateImage(UpdateProductCommand command, ProductImage oldImage) {
        if (!command.hasImage()) {
            log.debug("No image update requested");
            return null;
        }

        log.debug("Uploading new image: {}", command.imageName());

        try {
            // Upload new image
            ProductImage newImage = imageStorageService.upload(
                    command.imageName(),
                    command.imageData()
            );

            log.info("New image uploaded: {}", newImage.imageUrl());

            // Delete old image (if exists)
            if (oldImage != null) {

                imageStorageService.delete(oldImage);
                log.debug("Old image deleted: {}", oldImage.imageUrl());
            }

            return newImage;

        } catch (Exception e) {
            log.error("Failed to upload new image", e);
            throw new CommandException("Failed to upload product image: " + e.getMessage());
        }
    }
}
