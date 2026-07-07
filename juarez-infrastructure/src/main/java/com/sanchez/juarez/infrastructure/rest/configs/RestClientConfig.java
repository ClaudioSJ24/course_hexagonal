package com.sanchez.juarez.infrastructure.rest.configs;

import com.sanchez.juarez.infrastructure.rest.models.JsonplaceholderConfigModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestClient;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class RestClientConfig {
    private static final Logger log = LoggerFactory.getLogger(RestClientConfig.class);
    private final JsonplaceholderConfigModel jsonConfig;

    public RestClientConfig(JsonplaceholderConfigModel jsonConfig) {
        this.jsonConfig = jsonConfig;
    }

    @Bean(name = "jsonplaceholder")
    @ConditionalOnProperty(
            prefix = "jsonplaceholder",
            name = "enabled",
            havingValue = "true",
            matchIfMissing = true
    )
    public RestClient restClient() {
        return RestClient.builder()
                .baseUrl(jsonConfig.baseUrl())
                .requestInterceptors(interceptors -> {
                    interceptors.add(loggingInterceptor());
                    interceptors.add(errorLoggingInterceptor());
                })
                .defaultHeaders(headers -> {
                    headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
                    headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
                })
                .build();
    }


    private ClientHttpRequestInterceptor loggingInterceptor() {
        return (req, body, exe) -> {
            log.info("Calling Jsonplaceholder API");
            log.info("Method: {}", req.getMethod());
            log.info("URI: {}", req.getURI());
            log.info("Headers: {}", req.getHeaders());

            final long startTime = System.currentTimeMillis();
            final long endTime;
            try {
                var res = exe.execute(req, body);
                endTime = System.currentTimeMillis() - startTime;

                log.info("Status: {} ms", res.getStatusCode());
                log.info("Execution time: {} ms", endTime);
                return res;
            } catch (Exception e) {
                log.error("Error calling Jsonplaceholder API", e);
                throw e;
            }
        };
    }

    private ClientHttpRequestInterceptor errorLoggingInterceptor() {
        return (req, body, exe) -> {
            try {
                return exe.execute(req, body);
            } catch (Exception e) {
                log.error("Error message: {}", e.getMessage());
                throw e;
            }
        };

    }
}
