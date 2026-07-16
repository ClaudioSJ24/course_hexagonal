package com.sanchez.juarez.infrastructure.mongo.mappers;


import com.juarez.domain.entities.views.CatalogView;
import com.juarez.domain.entities.views.ItemsView;
import com.sanchez.juarez.infrastructure.mongo.documents.CatalogDocument;
import com.sanchez.juarez.infrastructure.mongo.documents.CatalogItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface CatalogMapper {

    @Mapping(source = "catalogType", target = "type")
    CatalogView toView(CatalogDocument document);

    ItemsView toItemView(CatalogItem item);
}
