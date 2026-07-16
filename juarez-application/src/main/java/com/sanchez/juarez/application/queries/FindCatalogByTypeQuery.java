package com.sanchez.juarez.application.queries;


import com.juarez.domain.entities.views.CatalogView;
import com.juarez.domain.ports.repositories.CatalogRepositoryPort;
import com.sanchez.juarez.common.enums.CatalogType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service

public class FindCatalogByTypeQuery {

    private static final Logger log = LogManager.getLogger(FindCatalogByTypeQuery.class);
    private final CatalogRepositoryPort catalogRepository;

    public FindCatalogByTypeQuery(CatalogRepositoryPort catalogRepository) {
        this.catalogRepository = catalogRepository;
    }

    public Optional<CatalogView> execute(CatalogType catalogType) {

        log.info("Execute FindCatalogByTypeQuery");
        return this.catalogRepository.findByType(catalogType);
    }

}

