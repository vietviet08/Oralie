package com.oralie.gatewayserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;

@SpringBootApplication
public class GatewayserverApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayserverApplication.class, args);
    }

    @Bean
    public RouteLocator RouteLocatorCustomConfig(RouteLocatorBuilder routeLocatorBuilder) {
        return routeLocatorBuilder.routes()
                .route(p -> p
                        .path("/api/accounts/**")
                        .filters(f -> f
                                .rewritePath("/api/accounts/?(?<remaining>.*)", "/${remaining}")
                                .circuitBreaker(c -> c.setName("ACCOUNTS-CIRCUIT-BREAKER")
                                        .setFallbackUri("forward:/accountsServiceFallback")
                                )
                        )
                        .uri("lb://ACCOUNTS")
                )
                .build();
    }
}
