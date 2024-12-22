package com.oralie.rates.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
@RequiredArgsConstructor
public class OrderService extends AbstractCircuitBreakFallbackHandler {

    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

    private final RestClient restClient;

    @Value("${url.orders}")
    private String URL_ORDERS;

    @Retry(name = "restRetry")
    @CircuitBreaker(name = "restCircuitBreaker", fallbackMethod = "handleBooleanFallback")
    public boolean checkIsRated(Long orderItemId) {
        log.info("Checking order item by id: {}", orderItemId.toString());

        final URI url = UriComponentsBuilder
                .fromHttpUrl(URL_ORDERS)
                .path("/store/orders/rated/{orderItemId}")
                .buildAndExpand(orderItemId)
                .toUri();

        try {
            return Boolean.TRUE.equals(restClient.get()
                    .uri(url)
                    .retrieve()
                    .body(Boolean.class));
        } catch (Exception ex) {
            log.error("Error fetching product with id: {}", orderItemId, ex);
            throw ex;
        }
    }

    @Retry(name = "restRetry")
    @CircuitBreaker(name = "restCircuitBreaker", fallbackMethod = "handleVoidFallback")
    public void updateRateStatus(Long orderItemId) {
        log.info("Checking order item by id: {}", orderItemId.toString());

        final URI url = UriComponentsBuilder
                .fromHttpUrl(URL_ORDERS)
                .path("/store/orders/rated/{orderItemId}")
                .buildAndExpand(orderItemId)
                .toUri();

        restClient.put()
                .uri(url)
                .retrieve()
                .body(Void.class);
    }

    protected boolean handleBooleanFallback(Throwable throwable) throws Throwable {
        log.error("Fallback triggered due to: {}", throwable.getMessage(), throwable);
        return handleTypedFallback(throwable);
    }

    protected void handleVoidFallback(Throwable throwable) throws Throwable {
        log.error("Fallback triggered due to: {}", throwable.getMessage(), throwable);
        handleTypedFallback(throwable);
    }
}