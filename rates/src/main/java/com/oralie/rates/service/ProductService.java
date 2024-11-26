package com.oralie.rates.service;

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

@Service
@RequiredArgsConstructor
public class ProductService extends AbstractCircuitBreakFallbackHandler{
	
	private static final Logger log = LoggerFactory.getLogger(ProductService.class);
 	
 	private final RestClient restClient;

        @Value("${url.product}")
        private String URL_PRODUCT;

	@Retry(name = "restRetry")
        @CircuitBreaker(name = "restCircuitBreaker", fallbackMethod = "handleBooleanFallback")
	public boolean existingProductByProductId(Long productId){
		log.info("Checking product by id: {}", productId.toString());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getCredentials() == null) {
            throw new UnauthorizedException("Authentication or JWT token is missing");
        }

        final String jwtToken = authentication.getCredentials().toString();

        final URI url = UriComponentsBuilder
                .fromHttpUrl(URL_PRODUCT)
                .pathSegment("store", "products", "existingById", productId.toString())
                .build()
                .toUri();

        return restClient.get()
                .uri(url)
                .headers(headers -> headers.setBearerAuth(jwtToken))
                .retrieve()
                .body(ProductBaseResponse.class);
	}
	protected ProductBaseResponse handleBooleanFallback(Throwable throwable) {
        log.error("Fallback triggered due to: {}", throwable.getMessage(), throwable);
        return handleTypedFallback(throwable);
    }
}	