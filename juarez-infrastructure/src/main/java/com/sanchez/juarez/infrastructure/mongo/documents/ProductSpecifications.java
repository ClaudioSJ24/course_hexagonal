package com.sanchez.juarez.infrastructure.mongo.documents;

public record ProductSpecifications(
        String processor,
        String ram,
        String storage,
        String display,
        String weight
) {
}
