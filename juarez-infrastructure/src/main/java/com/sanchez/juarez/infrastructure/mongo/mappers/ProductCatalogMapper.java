package com.sanchez.juarez.infrastructure.mongo.mappers;

import com.juarez.domain.entities.views.ProductView;
import com.sanchez.juarez.infrastructure.mongo.documents.ProductInCatalogDocument;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface ProductCatalogMapper {
    @Mapping(source = "currency", target = "money")
    ProductView toView(ProductInCatalogDocument document);
}
