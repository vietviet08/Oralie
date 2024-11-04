package com.oralie.carts.service;

import com.oralie.carts.dto.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
@RequiredArgsConstructor
public class ProductService {

    private static final String URL_PRODUCT = "http://localhost:8081";
    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

    private final RestClient restClient;

    public ProductResponse getProductById(Long productId) {
        log.info("Fetching product with id: {}", productId);

        final String jwtToken = SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();

        final URI url = UriComponentsBuilder
                .fromHttpUrl(URL_PRODUCT)
                .path("/store/products/id/{id}")
                .buildAndExpand(productId)
                .toUri();

        return restClient.get()
                .uri(url)
                .header("Authorization", "Bearer " + jwtToken)
                .retrieve()
                .body(ProductResponse.class);
    }

}
