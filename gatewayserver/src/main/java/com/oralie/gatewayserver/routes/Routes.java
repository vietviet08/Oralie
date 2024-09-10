package com.oralie.gatewayserver.routes;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.server.mvc.filter.CircuitBreakerFilterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import java.net.URI;

import static org.springframework.cloud.gateway.server.mvc.filter.FilterFunctions.setPath;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;

@Configuration
public class Routes {

    @Value("${services.accounts.url}")
    private String accountsServiceUrl;

    @Bean
    public RouterFunction<ServerResponse> productServiceRoute() {
        return GatewayRouterFunctions.route("accounts_service")
                .route(RequestPredicates.path("/api/accounts"), HandlerFunctions.http(accountsServiceUrl))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("accountsServiceCircuitBreaker",
                        URI.create("forward:/fallbackRoute")))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> productServiceSwaggerRoute() {
        return GatewayRouterFunctions.route("accounts_service_swagger")
                .route(RequestPredicates.path("/aggregate/accounts-service/v3/api-docs"), HandlerFunctions.http(accountsServiceUrl))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("accountsServiceSwaggerCircuitBreaker",
                        URI.create("forward:/fallbackRoute")))
                .filter(setPath("/api-docs"))
                .build();
    }


    @Bean
    public RouterFunction<ServerResponse> fallbackRoute() {
        return route("fallbackRoute")
                .GET("/fallbackRoute", request -> ServerResponse.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .body("Service Unavailable, please try again later"))
                .build();
    }
}
