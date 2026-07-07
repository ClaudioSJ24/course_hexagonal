package com.sanchez.juarez.infrastructure.jpa.repositories;

import com.sanchez.juarez.infrastructure.jpa.entities.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderRepository extends JpaRepository<OrderEntity, UUID> {
}
