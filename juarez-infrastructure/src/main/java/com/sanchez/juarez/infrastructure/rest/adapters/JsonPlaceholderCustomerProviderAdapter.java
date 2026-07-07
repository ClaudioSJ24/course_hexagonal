package com.sanchez.juarez.infrastructure.rest.adapters;

import com.juarez.domain.entities.customer.CustomerInfo;
import com.juarez.domain.ports.services.CustomerProviderServicePort;
import com.sanchez.juarez.infrastructure.rest.dtos.UserDTO;
import com.sanchez.juarez.infrastructure.rest.mappers.CustomerMapper;
import com.sanchez.juarez.infrastructure.rest.models.JsonplaceholderConfigModel;
import lombok.extern.slf4j.Slf4j;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Optional;
@Service
@Slf4j
public class JsonPlaceholderCustomerProviderAdapter implements CustomerProviderServicePort {

    private static final Logger log = LogManager.getLogger(JsonPlaceholderCustomerProviderAdapter.class);
    private final RestClient restClient;
    private final CustomerMapper customerMapper;
    private final String  endPoint;

    public JsonPlaceholderCustomerProviderAdapter(
            @Qualifier("jsonplaceholder") RestClient restClient,
            CustomerMapper customerMapper,
            JsonplaceholderConfigModel jsonConfig) {
        this.restClient = restClient;
        this.customerMapper = customerMapper;
        this.endPoint = jsonConfig.usersEndpoint();
    }

    @Override
    public Optional<CustomerInfo> findById(Long id) {
        log.info("findById: {}", id);
        try {
            final UserDTO response = this.restClient
                    .get()
                    .uri(endPoint, id)
                    .retrieve()
                    .onStatus( HttpStatusCode::is4xxClientError,(req, res) -> {
                        log.error("Error on client side: {}", req);
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                        log.error("Error on server side: {}", req);
                    })
                    .body(UserDTO.class);

            if (response == null) {
                return  Optional.empty();
            }
            log.info("User found: {}", response);

            return Optional.of(this.customerMapper.toCustomerInfo(response));
        }catch (RestClientException rce) {
            log.error("Error on findById while call API", rce);
            return Optional.empty();
        } catch (Exception e) {
            log.error("Error on findById ", e);
            return Optional.empty();
        }


    }

    @Override
    public boolean existsById(Long id) {
        log.info("ExistById: {}",id);
        return false;
    }
}
