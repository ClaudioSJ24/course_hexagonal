package com.sanchez.juarez.infrastructure.aws.models;

import jakarta.validation.constraints.NotBlank;

public record AwsConfigModel(

        @NotBlank(message = "AWS S3 endpoint must not be blank")
        String endpoint,

        @NotBlank(message = "AWS region must not be blank")
        String region,

        @NotBlank(message = "AWS access key must not be blank")
        String accessKey,

        @NotBlank(message = "AWS secret key must not be blank")
        String secretKey,

        @NotBlank(message = "AWS S3 bucket name must not be blank")
        String bucketName,

        Boolean pathStyleEnabled
) {
        public String getBucketUrl(){
                return String.format("%/%", endpoint, bucketName);
        }
}
