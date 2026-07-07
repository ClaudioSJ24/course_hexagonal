package com.sanchez.juarez.infrastructure.mongo.repositories;

import com.sanchez.juarez.infrastructure.mongo.documents.CatalogDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CatalogRepository extends MongoRepository<CatalogDocument, String> {
}
