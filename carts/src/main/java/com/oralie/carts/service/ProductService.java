package com.oralie.carts.service;

import com.oralie.carts.dto.client.search.ProductBaseResponse;
import com.oralie.carts.dto.client.products.ProductOptionResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService extends AbstractCircuitBreakFallbackHandler {

    @Value("${url.products}")
    private String URL_PRODUCT;
    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

    private final RestClient restClient;

    @Retry(name = "productRetry")
    @CircuitBreaker(name = "productCircuitBreaker", fallbackMethod = "handleProductBaseResponseFallBack")
    public ProductBaseResponse getProductById(Long productId) {
        log.info("Fetching product with id: {}", productId);

        final String jwt = ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .getTokenValue();

        log.info("JWT Token: {}", jwt);

        final URI url = UriComponentsBuilder
                .fromHttpUrl(URL_PRODUCT)
                .path("/store/products/product-base/{productId}")
                .buildAndExpand(productId)
                .toUri();

        try {
            return restClient.get()
                    .uri(url)
                    .headers(h -> h.setBearerAuth(jwt))
                    .retrieve()
                    .body(ProductBaseResponse.class);
        } catch (Exception ex) {
            log.error("Error fetching product with id: {}", productId, ex);
            throw ex;
        }
    }

    @Retry(name = "productRetry")
    @CircuitBreaker(name = "productCircuitBreaker", fallbackMethod = "handleListProductOptionResponseFallBack")
    public List<ProductOptionResponse> getProductOptions(Long productId) {
        log.info("Fetching product options with id: {}", productId);

        final String jwt = ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .getTokenValue();

        final URI url = UriComponentsBuilder
                .fromHttpUrl(URL_PRODUCT)
                .path("/store/products/options/{productId}")
                .buildAndExpand(productId)
                .toUri();

        try {
            return restClient.get()
                    .uri(url)
                    .headers(h -> h.setBearerAuth(jwt))
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<ProductOptionResponse>>() {});
        } catch (Exception ex) {
            log.error("Error fetching product options with id: {}", productId, ex);
            throw ex;
        }
    }


    protected ProductBaseResponse handleProductBaseResponseFallBack(Long productId, Throwable throwable) {
        log.error("Fallback method called for product id: {} due to exception: {}", productId, throwable.getMessage());
        return new ProductBaseResponse();
    }

    protected ProductBaseResponse handleListProductOptionResponseFallBack(Long productId, Throwable throwable) {
        log.error("Fallback method called for product id: {} due to exception: {}", productId, throwable.getMessage());
        return new ProductBaseResponse();
    }

    //webflux
//        final URI url = UriComponentsBuilder
//                .fromHttpUrl(URL_PRODUCT)
//                .path("/store/products/product-base/{productId}")
//                .buildAndExpand(productId)
//                .toUri();
//
//        return webClient.get()
//                .uri(url)
//                .header("Authorization", "Bearer " + jwt)
//                .retrieve()
//                .bodyToMono(ProductBaseResponse.class)
//                .block();
}
