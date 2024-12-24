package com.oralie.rates.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
@RequiredArgsConstructor
public class OrderService extends AbstractCircuitBreakFallbackHandler {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final RestClient restClient;

    @Value("${url.orders}")
    private String URL_ORDERS;

    @Retry(name = "restRetry")
    @CircuitBreaker(name = "restCircuitBreaker", fallbackMethod = "handleStringFallback")
    public String checkIsRated(Long orderItemId) {
        log.info("Checking order item by id: {}", orderItemId);

        final String jwtToken = ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .getTokenValue();

        final URI url = UriComponentsBuilder
                .fromHttpUrl(URL_ORDERS)
                .path("/store/orders/rated/{orderItemId}")
                .buildAndExpand(orderItemId)
                .toUri();

        try {
            return restClient.get()
                    .uri(url)
                    .headers(h -> h.setBearerAuth(jwtToken))
                    .retrieve()
                    .body(String.class);
        } catch (HttpClientErrorException ex) {
            if(ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                log.warn("Order item {} not found at order service. Returning 'false'", orderItemId);
                return "false";
            }
            log.error("Error fetching order item with id: {}", orderItemId, ex);
            throw ex;
        } catch (Exception ex) {
            log.error("Error fetching order item with id: {}", orderItemId, ex);
            throw ex;
        }
    }

    @Retry(name = "restRetry")
    @CircuitBreaker(name = "restCircuitBreaker", fallbackMethod = "handleVoidFallback")
    public void updateRateStatus(Long orderItemId) {
        log.info("Checking order item by id: {}", orderItemId.toString());

        final String jwtToken = ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .getTokenValue();

        final URI url = UriComponentsBuilder
                .fromHttpUrl(URL_ORDERS)
                .path("/store/orders/rated/{orderItemId}")
                .buildAndExpand(orderItemId)
                .toUri();

        try {
            restClient.put()
                    .uri(url)
                    .headers(h -> h.setBearerAuth(jwtToken))
                    .retrieve()
                    .body(Void.class);
        } catch (Exception ex) {
            log.error("Error updating rate status for order item with id: {}", orderItemId, ex);
        }
    }

    protected boolean handleBooleanFallback(Throwable throwable) throws Throwable {
        log.error("Fallback triggered due to: {}", throwable.getMessage(), throwable);
        handleError(throwable);
        return false;
    }

    protected void handleVoidFallback(Throwable throwable) throws Throwable {
        log.error("Fallback triggered due to: {}", throwable.getMessage(), throwable);
        handleError(throwable);
    }

    private void handleError(Throwable throwable) throws Throwable {
        log.error("Circuit breaker records an error. Detail {}", throwable.getMessage());
        throw throwable;
    }
}