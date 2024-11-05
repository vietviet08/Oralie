package com.oralie.carts.service;

import com.oralie.carts.dto.ProductResponse;
import com.oralie.carts.dto.response.ProductBaseResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService extends AbstractCircuitBreakFallbackHandler {

    private static final String URL_PRODUCT = "http://localhost:8081";
    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

    private final RestClient restClient;

    private final WebClient webClient;

    @Retry(name = "productRetry")
    @CircuitBreaker(name = "productCircuitBreaker", fallbackMethod = "handleProductBaseResponseFallBack")
    public ProductBaseResponse getProductById(Long productId) {
        log.info("Fetching product with id: {}", productId);

//        final String jwtToken = SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();

        final String jwt = ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .getTokenValue();

        log.info("JWT Token: {}", jwt);

//        final URI url = UriComponentsBuilder
//                .fromHttpUrl(URL_PRODUCT)
//                .path("/store/products/id/{id}")
//                .buildAndExpand(productId)
//                .toUri();
//
//        return restClient.get()
//                .uri(url)
//                .header("Authorization", "Bearer " + jwt)
//                .retrieve()
//                .body(ProductResponse.class);

        //webflux
        final URI url = UriComponentsBuilder
                .fromHttpUrl(URL_PRODUCT)
                .path("/store/products/product-base/{productId}")
                .buildAndExpand(productId)
                .toUri();

        return webClient.get()
                .uri(url)
                .header("Authorization", "Bearer " + jwt)
                .retrieve()
                .bodyToMono(ProductBaseResponse.class)
                .block();
    }


    protected ProductBaseResponse handleProductBaseResponseFallBack(Throwable throwable) throws Throwable {
        return handleTypedFallback(throwable);
    }
}
