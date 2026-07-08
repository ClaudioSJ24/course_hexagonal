package com.sanchez.juarez.infrastructure.jpa.adapters;

import com.juarez.domain.entities.product.ProductId;
import com.juarez.domain.entities.product.ProductRoot;
import com.juarez.domain.ports.repositories.ProductRepositoryPort;
import com.sanchez.juarez.infrastructure.jpa.mappers.ProductJpaMapper;
import com.sanchez.juarez.infrastructure.jpa.repositories.ProductRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class ProductRepositoryAdapter implements ProductRepositoryPort {

    private final ProductRepository productRepository;
    private final ProductJpaMapper productJpaMapper;

    public ProductRepositoryAdapter(ProductRepository productRepository, ProductJpaMapper productJpaMapper) {
        this.productRepository = productRepository;
        this.productJpaMapper = productJpaMapper;
    }

    @Override
    public ProductRoot save(ProductRoot product) {
        return null;
    }

    @Override
    public Optional<ProductRoot> findAllById(ProductId id) {
        return Optional.empty();
    }

    @Override
    public Optional<ProductRoot> findBySku(String sku) {
        return Optional.empty();
    }

    @Override
    public void delete(ProductRoot product) {

    }
}
