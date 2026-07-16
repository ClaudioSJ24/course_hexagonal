package com.sanchez.juarez.application.queries;

import com.juarez.domain.entities.views.ProductView;
import com.juarez.domain.ports.repositories.ProductCatalogRepositoryPort;
import com.sanchez.juarez.application.exceptions.QueryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class FindProductBySkuQuery {

    private static final Logger log = LoggerFactory.getLogger(FindProductBySkuQuery.class);
    private final ProductCatalogRepositoryPort productCatalogRepository;

    public FindProductBySkuQuery(ProductCatalogRepositoryPort productCatalogRepository) {
        this.productCatalogRepository = productCatalogRepository;
    }

    public Optional<ProductView> execute(String sku) {

        log.info("Execute FindProductBySkuQuery");
        try {
            return productCatalogRepository.findBySku(sku);
        } catch (RuntimeException e) {
            throw  new QueryException("Error executing ProductCatalogRepositoryPort");
        }
    }
}
