package com.sanchez.juarez.application.queries;

import com.juarez.domain.entities.views.ProductView;
import com.juarez.domain.ports.repositories.ProductCatalogRepositoryPort;
import com.sanchez.juarez.application.exceptions.QueryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

public class FindProductActiveQuery {

    private static final Logger log = LoggerFactory.getLogger(FindProductActiveQuery.class);
    private final ProductCatalogRepositoryPort productCatalogRepositoryPort;

    public FindProductActiveQuery(ProductCatalogRepositoryPort productCatalogRepositoryPort) {
        this.productCatalogRepositoryPort = productCatalogRepositoryPort;
    }

    public List<ProductView> execute(){

        log.info("Execute FindProductActiveQuery");

        try {

            return  this.productCatalogRepositoryPort.findActive();

        }catch (RuntimeException e) {
            throw  new QueryException("Error executing FindProductActiveQuery");
        }
    }



}
