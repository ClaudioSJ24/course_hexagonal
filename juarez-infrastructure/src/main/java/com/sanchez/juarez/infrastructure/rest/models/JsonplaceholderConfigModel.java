package com.sanchez.juarez.infrastructure.rest.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record JsonplaceholderConfigModel(
        @NotBlank(message = "JsonPlaceholder base URL must not be blank")
        String baseUrl,

        @NotBlank(message = "JsonPlaceholder users endpoint must not be blank")
        String usersEndpoint,

        @Positive(message = "Connect timeout must be greater than zero")
        int connectTimeout,

        @Positive(message = "Read timeout must be greater than zero")
        int readTimeout,

        @NotNull(message = "JsonPlaceholder enabled flag must not be null")
        Boolean enabled
) {
    public String usersUrl(){
        return baseUrl + usersEndpoint;
    }
}
