package com.sanchez.juarez.application.queries;

import com.juarez.domain.entities.views.ProductView;
import com.juarez.domain.ports.repositories.ProductCatalogRepositoryPort;
import com.sanchez.juarez.application.exceptions.QueryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class FindProductByIdQuery {

    private static final Logger log = LoggerFactory.getLogger(FindProductByIdQuery.class);
    private final ProductCatalogRepositoryPort productCatalogRepository;

    public FindProductByIdQuery(ProductCatalogRepositoryPort productCatalogRepository) {
        this.productCatalogRepository = productCatalogRepository;
    }

    public Optional<ProductView> execute(String id) {

        log.info("Execute FindProductByIdQuery id");
        try {
            return productCatalogRepository.findById(id);
        } catch (RuntimeException e) {
            throw  new QueryException("Error executing FindProductByIdQuery");
        }

    }
}
