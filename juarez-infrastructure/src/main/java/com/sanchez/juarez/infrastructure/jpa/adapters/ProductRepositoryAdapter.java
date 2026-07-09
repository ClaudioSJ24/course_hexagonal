package com.sanchez.juarez.infrastructure.jpa.adapters;

import com.juarez.domain.entities.product.ProductId;
import com.juarez.domain.entities.product.ProductRoot;
import com.juarez.domain.ports.repositories.ProductRepositoryPort;
import com.sanchez.juarez.infrastructure.jpa.entities.ProductEntity;
import com.sanchez.juarez.infrastructure.jpa.mappers.ProductJpaMapper;
import com.sanchez.juarez.infrastructure.jpa.repositories.ProductRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class ProductRepositoryAdapter implements ProductRepositoryPort {

    private static final Logger log = LogManager.getLogger(ProductRepositoryAdapter.class);
    private final ProductRepository productRepository;
    private final ProductJpaMapper productJpaMapper;

    public ProductRepositoryAdapter(ProductRepository productRepository, ProductJpaMapper productJpaMapper) {
        this.productRepository = productRepository;
        this.productJpaMapper = productJpaMapper;
    }

    @Override
    public ProductRoot save(ProductRoot product) {

        log.info("Saving product {}", product);
        try{

            ProductEntity productEntity = this.productJpaMapper.toEntity(product);
            productEntity.setId(product.getId().value());
            log.info("try to saving product {}", product.getSku());

            ProductEntity productSaved = this.productRepository.save(productEntity);
            log.info("saved product SUCCESS {}", productSaved);

            return this.productJpaMapper.toDomain(productSaved);

        } catch (Exception e) {
            log.error("Error on persist product", e);
            throw new IllegalStateException(e);
        }


    }

    @Override
    public Optional<ProductRoot> findAllById(ProductId id) {

        log.debug("Finding product by ID: {}", id.value());

        try {

            Optional<ProductEntity> productEntityOptional = this.productRepository.findById(id.value());

            if (productEntityOptional.isEmpty()) {

                log.debug("Product not found with ID: {}", id.value());
                return Optional.empty();
            }

            ProductEntity  productEntity = productEntityOptional.get();
            log.debug("Product found with ID: {} and SKU: {}",
                    productEntity.getId(),
                    productEntity.getSku());

            ProductRoot domain = this.productJpaMapper.toDomain(productEntity);
            log.debug("Product rehydrated successfully");

            return Optional.of(domain);


        } catch (Exception e) {
            log.error("Failed to find product by ID: {}", id.value(), e);
            return Optional.empty();
        }

    }

    @Override
    public Optional<ProductRoot> findBySku(String sku) {

        log.debug("Finding product by SKU: {}", sku);

        try {

            Optional<ProductEntity> productEntityOptional = this.productRepository.findBySku(sku);

            if (productEntityOptional.isEmpty()) {
                log.debug("Product not found with SKU: {}", sku);
                return Optional.empty();
            }

            ProductEntity productEntity = productEntityOptional.get();
            log.debug("Product found with SKU: {} (ID: {})",
                    productEntity.getSku(),
                    productEntity.getId());

            ProductRoot domain = this.productJpaMapper.toDomain(productEntity);

            return Optional.of(domain);


        }catch (Exception e) {
            log.error("Failed to find product by SKU: {}", sku, e);
            return Optional.empty();
        }

    }

    @Override
    public void delete(ProductRoot product) {

        log.info("Deleting product with ID: {}", product.getId().value());

        try {

            this.productRepository.deleteById(product.getId().value());
            log.info("Product deleted successfully with ID: {}", product.getId().value());

        }catch (Exception e) {
            log.error("Failed to delete product with ID: {}", product.getId().value(), e);
            throw new IllegalStateException("Failed to delete product: " + e.getMessage(), e);
        }

    }
}
