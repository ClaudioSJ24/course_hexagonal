package com.sanchez.juarez.infrastructure.mongo.repositories;

import com.sanchez.juarez.common.enums.CatalogType;
import com.sanchez.juarez.infrastructure.mongo.documents.CatalogDocument;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CatalogRepository extends MongoRepository<CatalogDocument, String> {

    Optional<CatalogDocument> findByCatalogType(CatalogType type);
}
