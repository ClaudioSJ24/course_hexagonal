package com.sanchez.juarez.application.commands.product;

import jakarta.validation.constraints.NotBlank;

/**
 * Command to deactivate a product.
 * Deactivated products are not deleted but marked as inactive.
 * They won't appear in public searches but remain in the system for historical records.
 *
 * @param productId Product ID to deactivate
 */
public record DesactivateProductCommand(
        @NotBlank(message = "Product ID cannot be null or blank")
        String productId
) {
}
