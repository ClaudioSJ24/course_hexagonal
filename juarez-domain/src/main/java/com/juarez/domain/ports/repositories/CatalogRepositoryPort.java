package com.juarez.domain.ports.repositories;

import com.juarez.domain.views.CatalogView;
import com.juarez.domain.views.ItemsView;
import com.sanchez.juarez.common.enums.CatalogType;

import java.util.List;
import java.util.Optional;

/**
 * Port read-only for Catalog
 */
public interface CatalogRepositoryPort {
    Optional<CatalogView> findByType(CatalogType type);
    List<ItemsView> findItemsByType(CatalogType type);
    Optional<ItemsView> findItemByTypeAndCode(CatalogType type, String code);
}
