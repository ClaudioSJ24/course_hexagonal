package com.sanchez.juarez.application.queries;

import com.juarez.domain.entities.views.ProductView;
import com.juarez.domain.ports.repositories.ProductCatalogRepositoryPort;
import com.sanchez.juarez.application.exceptions.QueryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class FindProductByCategory {
    private static final Logger log = LoggerFactory.getLogger(FindProductByCategory.class);
    private final ProductCatalogRepositoryPort productCatalogRepository;

    public FindProductByCategory(ProductCatalogRepositoryPort productCatalogRepository) {
        this.productCatalogRepository = productCatalogRepository;
    }


    public List<ProductView> execute(String category) {

        log.info("Execute FindProductByCategory");

        try {
            return productCatalogRepository.findByCategory(category);
        } catch (RuntimeException e) {
            throw  new QueryException("Error executing ProductCatalogRepositoryPort");
        }
    }

}
