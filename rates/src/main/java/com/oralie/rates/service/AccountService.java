package com.oralie.rates.service;


import com.oralie.rates.dto.client.UserResponse;
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
public class AccountService extends AbstractCircuitBreakFallbackHandler {

    private static final Logger log = LoggerFactory.getLogger(AccountService.class);

    private final RestClient restClient;

    @Value("${url.accounts}")
    private String URL_ACCOUNT;

    @Retry(name = "restRetry")
    @CircuitBreaker(name = "restCircuitBreaker", fallbackMethod = "handleBooleanFallback")
    public boolean existingAccountByUserId(String userId) {
        log.info("Checking user by userId: {}", userId);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getCredentials() == null) {
            log.error("Authentication or credentials are null");
            return false;
        }
        final String jwtToken = authentication.getCredentials().toString();

        final URI url = UriComponentsBuilder
                .fromHttpUrl(URL_ACCOUNT)
                .pathSegment("store", "keycloak", "existing", userId)
                .build()
                .toUri();

        ResponseEntity<Boolean> response = restClient.get()
                .uri(url)
                .headers(headers -> headers.setBearerAuth(jwtToken))
                .retrieve()
                .toEntity(Boolean.class);

        return response.getBody() != null && response.getBody();
    }

    @Retry(name = "restRetry")
    @CircuitBreaker(name = "restCircuitBreaker", fallbackMethod = "handleUserResponseFallback")
    public UserResponse getUserByUserId(String userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getCredentials() == null) {
            log.error("Authentication or credentials are null");
            throw new RuntimeException("Authentication or credentials are null");
        }
        final String jwtToken = authentication.getCredentials().toString();

        final URI url = UriComponentsBuilder
                .fromHttpUrl(URL_ACCOUNT)
                .pathSegment("store", "keycloak", "users", userId)
                .build()
                .toUri();

        ResponseEntity<UserResponse> response = restClient.get()
                .uri(url)
                .headers(headers -> headers.setBearerAuth(jwtToken))
                .retrieve()
                .toEntity(UserResponse.class);

        return response.getBody();
    }

    protected Boolean handleBooleanFallback(Throwable throwable) {
        log.error("Fallback triggered due to: {}", throwable.getMessage(), throwable);
        return false;
    }

    protected UserResponse handleUserResponseFallback(Throwable throwable) {
        log.error("Fallback triggered due to: {}", throwable.getMessage(), throwable);
        return UserResponse.builder().build();
    }
}