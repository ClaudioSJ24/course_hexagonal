package com.sanchez.juarez.infrastructure.aws.configs;

import com.sanchez.juarez.infrastructure.aws.models.AwsConfigModel;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.net.URI;
@Slf4j
@Configuration

public class S3BucketConfig {
    private static final Logger log = LoggerFactory.getLogger(S3BucketConfig.class);

    @Bean
    public S3Client s3Client(AwsConfigModel awsConfig) {
        log.info("Configuring AWS S3 bucket");

        var credentials = AwsBasicCredentials.create(
                awsConfig.accessKey(),
                awsConfig.secretKey()
        );

        var s3Config = S3Configuration.builder()
                .pathStyleAccessEnabled(awsConfig.pathStyleEnabled())
                .build();

        var s3ClientBuilder = S3Client.builder()
                .endpointOverride(URI.create(awsConfig.endpoint()))
                .region(Region.of(awsConfig.region()))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .serviceConfiguration(s3Config);

        return s3ClientBuilder.build();
    }
}
