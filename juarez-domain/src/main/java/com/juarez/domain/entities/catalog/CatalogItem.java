package com.juarez.domain.entities.catalog;

import com.juarez.domain.common.Entity;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Map;

@ToString
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class CatalogItem extends Entity<String > {
    private final String code;
    private final String value;
    private final String description;
    private final int displayOrder;
    private final Map<String, Object> metadata;
    private boolean isActive;

    protected CatalogItem(
            String id,
            String code,
            String value,
            String description,
            int displayOrder,
            Map<String, Object> metadata
    ) {
        super(id);

        if (code == null || code.isEmpty()) {
            throw new IllegalArgumentException("Code cannot be null or empty");
        }

        this.code = code;
        this.value = value;
        this.description = description;
        this.displayOrder = displayOrder;
        this.metadata = metadata != null ? Map.copyOf(metadata) : Map.of();
        this.isActive = true;
    }

    public String getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }


}
