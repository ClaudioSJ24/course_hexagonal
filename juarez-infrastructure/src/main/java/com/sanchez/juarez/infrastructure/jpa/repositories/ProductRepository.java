package com.sanchez.juarez.infrastructure.jpa.repositories;

import com.sanchez.juarez.infrastructure.jpa.entities.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProductRepository extends JpaRepository<ProductEntity, UUID> {
}
