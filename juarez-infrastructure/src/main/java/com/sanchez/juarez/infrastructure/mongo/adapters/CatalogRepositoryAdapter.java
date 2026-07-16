package com.sanchez.juarez.infrastructure.mongo.adapters;

import com.juarez.domain.entities.views.CatalogView;
import com.juarez.domain.entities.views.ItemsView;
import com.juarez.domain.ports.repositories.CatalogRepositoryPort;


import com.sanchez.juarez.common.enums.CatalogType;
import com.sanchez.juarez.infrastructure.mongo.mappers.CatalogMapper;
import com.sanchez.juarez.infrastructure.mongo.repositories.CatalogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.List;
import java.util.Optional;

import static com.sanchez.juarez.common.constants.CacheConstants.*;

public class CatalogRepositoryAdapter implements CatalogRepositoryPort {

    private static final Logger log = LoggerFactory.getLogger(CatalogRepositoryAdapter.class);
    private final CatalogRepository catalogRepository;
    private final CatalogMapper catalogMapper;
    private final CacheManager cacheManager;

    public CatalogRepositoryAdapter(CatalogRepository catalogRepository, CatalogMapper catalogMapper, CacheManager cacheManager) {
        this.catalogRepository = catalogRepository;
        this.catalogMapper = catalogMapper;
        this.cacheManager = cacheManager;
    }

    @Override
    public Optional<CatalogView> findByType(CatalogType type) {
        log.info("Find catalog by type: {}", type);

        Cache cache = this.cacheManager.getCache(CACHE_CATALOGS_BY_TYPE);

        if (cache != null) {
            CatalogView catalogInCache = cache.get(type.name(), CatalogView.class);

            if (catalogInCache != null) {
                log.info("Found catalog in cache: {}", catalogInCache);
                return Optional.of(catalogInCache);
            }
        }
        return catalogRepository.findByCatalogType(type)
                .map(catalogMapper::toView);
    }

    @Override
    public List<ItemsView> findItemsByType(CatalogType type) {
        log.info("Find items catalog by type: {}", type);

        Cache cache = this.cacheManager.getCache(CACHE_CATALOGS_ITEMS);

        if (cache != null) {
            List<ItemsView> itemsInCache = cache.get(type.name(), List.class);

            if (itemsInCache != null) {
                log.info("Found catalog items in cache, total: {}", itemsInCache.size());
                return itemsInCache;
            }
        }

        return catalogRepository.findByCatalogType(type)
                .map(doc -> doc.getItems()
                        .stream()
                        .map(catalogMapper::toItemView)
                        .toList())
                .orElse(List.of());
    }

    @Override
    public Optional<ItemsView> findItemByTypeAndCode(CatalogType type, String code) {
        log.info("Find items catalog by type: {} & code: {}", type, code);

        return catalogRepository.findByCatalogType(type)
                .flatMap(doc -> doc.getItems()
                        .stream()
                        .filter(item -> item.code().equals(code))
                        .findFirst()
                        .map(catalogMapper::toItemView));
    }
}
