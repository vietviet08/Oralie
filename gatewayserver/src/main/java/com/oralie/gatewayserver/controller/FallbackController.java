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
}
