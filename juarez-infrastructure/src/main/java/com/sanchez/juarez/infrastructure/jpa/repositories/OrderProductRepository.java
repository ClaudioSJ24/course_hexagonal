package com.sanchez.juarez.infrastructure.jpa.repositories;

import com.sanchez.juarez.infrastructure.jpa.entities.OrderProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderProductRepository extends JpaRepository<OrderProductEntity, UUID> {
}
