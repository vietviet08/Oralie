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

    private static final Logger log = LoggerFactory.getLogger(ProductService.class);
    
    private final RestClient restClient;

    @Value("${url.product}")
    private String URL_PRODUCT;

    @Retry(name = "restRetry")
    @CircuitBreaker(name = "restCircuitBreaker", fallbackMethod = "handleProductBaseFallback")
    public ProductBaseResponse getProduct(Long id) {
        log.info("Getting product by id: {}", id.toString());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getCredentials() == null) {
            throw new UnauthorizedException("Authentication or JWT token is missing");
        }
        final String jwtToken = authentication.getCredentials().toString();

        final URI url = UriComponentsBuilder
                .fromHttpUrl(URL_PRODUCT)
                .pathSegment("store", "products", id.toString())
                .build()
                .toUri();

        return restClient.get()
                .uri(url)
                .headers(headers -> headers.setBearerAuth(jwtToken))
                .retrieve()
                .body(ProductBaseResponse.class);
    }

    @Retry(name = "restRetry")
    @CircuitBreaker(name = "restCircuitBreaker", fallbackMethod = "handleProductBaseFallback")
    public List<ProductBaseResponse> updateProductQuantity(List<ProductQuantityPost> productQuantityPosts) {
        log.info("Updating quantity of products: {} items", productQuantityPosts.size());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getCredentials() == null) {
            throw new UnauthorizedException("Authentication or JWT token is missing");
        }
        final String jwtToken = authentication.getCredentials().toString();

        final URI url = UriComponentsBuilder
                .fromHttpUrl(URL_PRODUCT)
                .pathSegment("dash", "products", "updateQuantity")
                .build()
                .toUri();

        return restClient.put()
                .uri(url)
                .headers(headers -> headers.setBearerAuth(jwtToken))
                .bodyValue(productQuantityPosts)
                .retrieve()
                .body(new ParameterizedTypeReference<List<ProductBaseResponse>>() {});
    }

    protected ProductBaseResponse handleProductBaseFallback(Throwable throwable) {
        log.error("Fallback triggered due to: {}", throwable.getMessage(), throwable);
        return handleTypedFallback(throwable);
    }
}
