package com.juarez.domain.entities.order;

import com.juarez.domain.common.Entity;
import com.juarez.domain.entities.product.ProductId;
import com.juarez.domain.entities.product.ProductRoot;
import com.juarez.domain.shared.Money;
import com.juarez.domain.shared.Quantity;


/**
 * OrderItem Entity.
 * Represents a line item in an order with product reference, quantity, and pricing.
 * Product name and unit price are snapshots taken at order creation time.
 */

public class OrderItem extends Entity<OrderItemId> {
    private ProductId productReference;
    private String productName;
    private Quantity quantity;
    private Money unitPrice;
    private Money subtotal;

    protected OrderItem() {
        super(null);
    }

    private OrderItem(
            OrderItemId orderItemId,
            ProductId productReference,
            String productName,
            Quantity quantity,
            Money unitPrice,
            Money subtotal) {
        super(orderItemId);
        this.productReference = productReference;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.subtotal = subtotal;
    }

    /**
     * Creates an OrderItem from a Product and quantity.
     *      * This is a snapshot: product name and price are frozen at order creation.
     *
     * @param product  the product to order
     * @param quantity the quantity to order
     * @return a new OrderItem instance
     * @throws IllegalArgumentException if product is inactive or has insufficient stock
     */
    public static OrderItem from(ProductRoot product, Quantity quantity){
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        if (quantity == null) {
            throw new IllegalArgumentException("Quantity cannot be null");
        }
        if (!product.isActive()) {
            throw new IllegalArgumentException("Cannot create order item for inactive product: " + product.getSku().value());
        }
        if (!product.hasAvailableStock(quantity.value())) {
            throw new IllegalArgumentException(
                    "Insufficient stock for product " + product.getSku().value() +
                            ". Required: " + quantity.value() + ", Available: " + product.getStock().value()
            );
        }
        OrderItemId orderItemId = OrderItemId.generate();
        Money unitPrice = product.getPrice();
        Money subTotal = unitPrice.multiply(quantity);

        return new OrderItem(
                orderItemId,
                product.getId(),
                product.getName().value(),
                quantity,
                unitPrice,
                subTotal
        );
    }

    /**
     * Calculates the subtotal (quantity * unitPrice).
     * This method exists for validation purposes.
     *
     * @return the calculated subtotal
     */
    public Money calculateSubtotal() {
        return this.unitPrice.multiply(this.quantity);
    }

    public ProductId getProductReference() {
        return productReference;
    }

    public String getProductName() {
        return productName;
    }

    public Quantity getQuantity() {
        return quantity;
    }

    public Money getUnitPrice() {
        return unitPrice;
    }

    public Money getSubtotal() {
        return subtotal;
    }
}
