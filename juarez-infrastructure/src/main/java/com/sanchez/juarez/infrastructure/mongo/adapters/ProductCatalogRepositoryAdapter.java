package com.sanchez.juarez.infrastructure.mongo.adapters;

import com.juarez.domain.entities.views.ProductView;
import com.juarez.domain.ports.repositories.ProductCatalogRepositoryPort;
import com.sanchez.juarez.infrastructure.mongo.mappers.ProductCatalogMapper;
import com.sanchez.juarez.infrastructure.mongo.repositories.ProductInCatalogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.sanchez.juarez.common.constants.CacheConstants.*;

@Repository
public class ProductCatalogRepositoryAdapter implements ProductCatalogRepositoryPort {


    private static final Logger log = LoggerFactory.getLogger(ProductCatalogRepositoryAdapter.class);
    private final ProductInCatalogRepository productInCatalogRepository;
    private final ProductCatalogMapper productCatalogMapper;
    private final CacheManager cacheManager;

    public ProductCatalogRepositoryAdapter(ProductInCatalogRepository productInCatalogRepository,
                                           ProductCatalogMapper productCatalogMapper,
                                           CacheManager cacheManager) {
        this.productInCatalogRepository = productInCatalogRepository;
        this.productCatalogMapper = productCatalogMapper;
        this.cacheManager = cacheManager;


    }

    @Override
    public Optional<ProductView> findById(String id) {
        Cache cache = cacheManager.getCache(CACHE_PRODUCTS_BY_ID);
        if (cache != null) {
            ProductView cached = cache.get(id, ProductView.class);
            if (cached != null) {
                log.debug("Cache HIT for productId: {}", id);
                return Optional.of(cached);
            }
        }

        return this.productInCatalogRepository.findById(id)
                .map(productCatalogMapper::toView);
    }

    @Override
    public Optional<ProductView> findBySku(String sku) {
        Cache cache = cacheManager.getCache(CACHE_PRODUCTS_BY_SKU);
        if (cache != null) {
            ProductView cached = cache.get(sku, ProductView.class);
            if (cached != null) {
                log.debug("Cache HIT for sku: {}", sku);
                return Optional.of(cached);
            }
        }

        return this.productInCatalogRepository.findBySku(sku)
                .map(productCatalogMapper::toView);
    }

    @Override
    public List<ProductView> findByText(String text) {
        log.info("Find product by text: {}", text);

        return this.productInCatalogRepository.findByTextAndActive(text)
                .stream().map(productCatalogMapper::toView)
                .toList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ProductView> findByCategory(String category) {
        Cache cache = cacheManager.getCache(CACHE_PRODUCTS_BY_CATEGORY);
        if (cache != null) {
            List<ProductView> cached = cache.get(category, List.class);
            if (cached != null) {
                log.debug("Cache HIT for category: {}", category);
                return cached;
            }
        }

        return this.productInCatalogRepository.findByCategoryIdAndActiveTrue(category)
                .stream().map(productCatalogMapper::toView)
                .toList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ProductView> findActive() {
        Cache cache = cacheManager.getCache(CACHE_PRODUCTS_ACTIVE);
        if (cache != null) {
            List<ProductView> cached = cache.get("all", List.class);
            if (cached != null) {
                log.debug("Cache for active products");
                return cached;
            }
        }

        return this.productInCatalogRepository.findByActiveTrueOrderByIdAsc()
                .stream().map(productCatalogMapper::toView)
                .toList();
    }
}
