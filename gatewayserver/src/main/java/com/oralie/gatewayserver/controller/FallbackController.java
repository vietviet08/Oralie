package com.oralie.gatewayserver.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class FallbackController {
    @GetMapping("/accountsServiceFallback")
    public Mono<String> accountsServiceFallback() {
        return Mono.just("Accounts Service is taking too long to respond or is down. Please try again later.");
    }

    @GetMapping("/productsServiceFallback")
    public Mono<String> productsServiceFallback() {
        return Mono.just("Products Service is taking too long to respond or is down. Please try again later.");
    }

    @GetMapping("/cartsServiceFallback")
    public Mono<String> cartsServiceFallback() {
        return Mono.just("Carts Service is taking too long to respond or is down. Please try again later.");
    }

    @GetMapping("/ordersServiceFallback")
    public Mono<String> ordersServiceFallback() {
        return Mono.just("Orders Service is taking too long to respond or is down. Please try again later.");
    }

    @GetMapping("/paymentServiceFallback")
    public Mono<String> paymentServiceFallback() {
        return Mono.just("Payment Service is taking too long to respond or is down. Please try again later.");
    }

    @GetMapping("/inventoryServiceFallback")
    public Mono<String> inventoryServiceFallback() {
        return Mono.just("Inventory Service is taking too long to respond or is down. Please try again later.");
    }

    @GetMapping("/ratesServiceFallback")
    public Mono<String> ratesServiceFallback() {
        return Mono.just("Rates Service is taking too long to respond or is down. Please try again later.");
    }

    @GetMapping("/socialServiceFallback")
    public Mono<String> socialServiceFallback() {
        return Mono.just("Social Service is taking too long to respond or is down. Please try again later.");
    }

    @GetMapping("/socialServiceFallback")
    public Mono<String> searchServiceFallback() {
        return Mono.just("Search Service is taking too long to respond or is down. Please try again later.");
    }

}
