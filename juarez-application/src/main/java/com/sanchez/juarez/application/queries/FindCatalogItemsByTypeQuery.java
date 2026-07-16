package com.sanchez.juarez.application.queries;

import com.juarez.domain.entities.views.ItemsView;
import com.juarez.domain.ports.repositories.CatalogRepositoryPort;
import com.sanchez.juarez.common.enums.CatalogType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FindCatalogItemsByTypeQuery {

    private static final Logger log = LogManager.getLogger(FindCatalogItemsByTypeQuery.class);
    private final CatalogRepositoryPort catalogRepositoryPort;

    public FindCatalogItemsByTypeQuery(CatalogRepositoryPort catalogRepositoryPort) {
        this.catalogRepositoryPort = catalogRepositoryPort;
    }

    public List<ItemsView> execute(CatalogType catalogType){
        log.info("Execute FindCatalogItemsByTypeQuery");

        return this.catalogRepositoryPort.findItemsByType(catalogType);
    }
}
