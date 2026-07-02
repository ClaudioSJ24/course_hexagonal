package com.juarez.domain.ports.repositories;

import com.juarez.domain.entities.product.ProductId;
import com.juarez.domain.entities.product.ProductRoot;

import java.util.Optional;
/**
 *  Port for storage o consult Products
 */
public interface ProductRepositoryPort {
    ProductRoot save(ProductRoot product);
    Optional<ProductRoot> findAllById(ProductId id);
    Optional<ProductRoot> findBySku(String sku);
    void delete(ProductRoot product);
}
