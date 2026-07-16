package com.sanchez.juarez.application.queries;

import com.juarez.domain.entities.views.ItemsView;
import com.juarez.domain.ports.repositories.CatalogRepositoryPort;
import com.sanchez.juarez.common.enums.CatalogType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FindCatalogItemByCodeQuery {

    private static final Logger log = LogManager.getLogger(FindCatalogItemByCodeQuery.class);
    private final CatalogRepositoryPort catalogRepositoryPort;


    public FindCatalogItemByCodeQuery(CatalogRepositoryPort catalogRepositoryPort) {
        this.catalogRepositoryPort = catalogRepositoryPort;
    }

    public Optional<ItemsView> execute(CatalogType catalogType, String code){

        log.info("Execute FindCatalogItemByCodeQuery");

        return this.catalogRepositoryPort.findItemByTypeAndCode(catalogType, code);
    }
}
