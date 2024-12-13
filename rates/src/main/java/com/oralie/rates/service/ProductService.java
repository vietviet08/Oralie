package com.oralie.rates.service;

import com.oralie.rates.constant.RateConstant;
import com.oralie.rates.dto.ProductBaseResponse;
import com.oralie.rates.dto.RateContactDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
@RequiredArgsConstructor
public class ProductService extends AbstractCircuitBreakFallbackHandler {

    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

    private final RestClient restClient;

    @Value("${url.products}")
    private String URL_PRODUCT;

    @Retry(name = "restRetry")
    @CircuitBreaker(name = "restCircuitBreaker", fallbackMethod = "handleBooleanFallback")
    public boolean existingProductByProductId(Long productId) {
        log.info("Checking product by id: {}", productId.toString());

        final URI url = UriComponentsBuilder
                .fromHttpUrl(URL_PRODUCT)
                .path("/store/products/existingById/{productId}")
                .buildAndExpand(productId)
                .toUri();

        try {
            return Boolean.TRUE.equals(restClient.get()
                    .uri(url)
                    .retrieve()
                    .body(Boolean.class));
        } catch (Exception ex) {
            log.error("Error fetching product with id: {}", productId, ex);
            throw ex;
        }
    }

    protected boolean handleBooleanFallback(Throwable throwable) throws Throwable {
        log.error("Fallback triggered due to: {}", throwable.getMessage(), throwable);
        return handleTypedFallback(throwable);
    }
}	