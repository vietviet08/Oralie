package com.oralie.inventory.service;

import com.oralie.inventory.dto.request.ProductQuantityPost;
import com.oralie.inventory.dto.response.ProductBaseResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService extends AbstractCircuitBreakFallbackHandler{

    private final RestClient restClient;

    @Value("${url.product}")
    private String URL_PRODUCT;

    @Retry(name = "restRetry")
    @CircuitBreaker(name = "restCircuitBreaker", fallbackMethod = "handleProductBaseFallback")
    public ProductBaseResponse getProduct(Long id) {
        final String jwt = ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .getTokenValue();
        final URI url = UriComponentsBuilder
                .fromHttpUrl(URL_PRODUCT)
                .path("/store/products/" + id)
                .buildAndExpand()
                .toUri();

        return restClient.get()
                .uri(url)
                .headers(h -> h.setBearerAuth(jwt))
                .retrieve()
                .body(ProductBaseResponse.class);

    }

    @Retry(name = "restRetry")
    @CircuitBreaker(name = "restCircuitBreaker", fallbackMethod = "handleProductBaseFallback")
    public List<ProductBaseResponse> updateProductQuantity(List<ProductQuantityPost> productQuantityPosts ) {
        final String jwt = ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .getTokenValue();
        final URI url = UriComponentsBuilder
                .fromHttpUrl(URL_PRODUCT)
                .path("/dash/products/updateQuantity" )
                .buildAndExpand()
                .toUri();

        return restClient.put()
                .uri(url)
                .headers(h -> h.setBearerAuth(jwt))
                .body(productQuantityPosts)
                .retrieve()
                .body(List<ProductBaseResponse.class>);

    }

    protected ProductBaseResponse handleProductBaseFallback(Throwable throwable) throws Throwable {
        return handleTypedFallback(throwable);
    }
}
