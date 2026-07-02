package com.juarez.domain.ports.services;

import com.juarez.domain.entities.customer.CustomerInfo;

import java.util.Optional;

/**
 *  Port for external service for JSONPlaceholder
 */
public interface CustomerProviderServicePort {

    Optional<CustomerInfo> findById(Long id);
    boolean existsById(Long id);
}