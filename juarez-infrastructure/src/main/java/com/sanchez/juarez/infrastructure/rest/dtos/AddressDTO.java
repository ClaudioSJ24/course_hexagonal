package com.sanchez.juarez.infrastructure.rest.dtos;

public record AddressDTO(
        String street,
        String suite,
        String city,
        String zipcode,
        GeoDTO geo
) {
}
