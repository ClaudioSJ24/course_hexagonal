package com.sanchez.juarez.infrastructure.mongo.repositories;

import com.sanchez.juarez.infrastructure.mongo.documents.ProductInCatalogDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductInCatalogRepository extends MongoRepository<ProductInCatalogDocument, String > {
}
