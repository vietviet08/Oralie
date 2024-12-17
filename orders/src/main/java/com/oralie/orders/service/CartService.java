package com.oralie.orders.service;

import com.oralie.orders.dto.response.client.CartResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
@RequiredArgsConstructor
public class CartService extends AbstractCircuitBreakFallbackHandler {

    private static final Logger log = LoggerFactory.getLogger(CartService.class);

    @Value("${url.carts}")
    private String URL_CARTS;

    private final RestClient restClient;

    @Retry(name = "cartRetry")
    @CircuitBreaker(name = "cartCircuitBreaker", fallbackMethod = "handleLongFallback")
    public Long getCartIdByUserId() {

        final String jwtToken = ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .getTokenValue();

        final URI url = UriComponentsBuilder
                .fromHttpUrl(URL_CARTS)
                .path("/store/carts/get-cart-id")
                .buildAndExpand()
                .toUri();

        return restClient.get()
                .uri(url)
                .headers(h -> h.setBearerAuth(jwtToken))
                .retrieve()
                .body(Long.class);
    }

    @Retry(name = "cartRetry")
    @CircuitBreaker(name = "cartCircuitBreaker", fallbackMethod = "handleCartResponseFallback")
    public CartResponse clearCart() {
        log.info("Clearing cart");

        final String jwtToken = ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .getTokenValue();

        final URI url = UriComponentsBuilder
                .fromHttpUrl(URL_CARTS)
                .path("/store/carts/clear")
                .buildAndExpand()
                .toUri();

        return restClient.delete()
                .uri(url)
                .headers(h -> h.setBearerAuth(jwtToken))
                .retrieve()
                .body(CartResponse.class);
    }

    protected Long handleLongFallback(Throwable throwable) throws Throwable {
        handleError(throwable);
        return null;
    }

    protected CartResponse handleCartResponseFallback(Throwable throwable) throws Throwable {
        handleError(throwable);
        return null;
    }

    private void handleError(Throwable throwable) throws Throwable {
        log.error("Circuit breaker records an error. Detail {}", throwable.getMessage());
        throw throwable;
    }

}
