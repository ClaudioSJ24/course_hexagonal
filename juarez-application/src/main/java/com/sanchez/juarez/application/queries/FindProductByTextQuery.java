package com.sanchez.juarez.application.queries;

import com.juarez.domain.entities.views.ProductView;
import com.juarez.domain.ports.repositories.ProductCatalogRepositoryPort;
import com.sanchez.juarez.application.exceptions.QueryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class FindProductByTextQuery {

    private static final Logger log = LoggerFactory.getLogger(FindProductByTextQuery.class);
    private final ProductCatalogRepositoryPort productCatalogRepository;

    public FindProductByTextQuery(ProductCatalogRepositoryPort productCatalogRepository) {
        this.productCatalogRepository = productCatalogRepository;
    }

    public List<ProductView> execute(String text) {

        log.info("Execute FindProductByTextQuery");
        try {
            return productCatalogRepository.findByText(text);
        } catch (RuntimeException e) {
            throw  new QueryException("Error executing FindProductByTextQuery");
        }
    }
}
