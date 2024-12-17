package com.oralie.gatewayserver.routes;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.gateway.config.GlobalCorsProperties;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Configuration
public class RouteConfig {

    @Bean
    public RouteLocator RouteLocatorCustomConfig(RouteLocatorBuilder routeLocatorBuilder) {
        return routeLocatorBuilder.routes()
                .route(p -> p
                        .path("/api/accounts/**")
                        .filters(f -> f.tokenRelay()
                                .rewritePath("/api/accounts/(?<segment>.*)", "/${segment}")
                                .circuitBreaker(c -> c.setName("ACCOUNTS-CIRCUIT-BREAKER")
                                        .setFallbackUri("forward:/accountsServiceFallback")
                                )
                        )
                        .uri("lb://ACCOUNTS")
                )
                .route(r -> r
                        .path("/aggregate/accounts-service/v3/api-docs/**")
                        .filters(f -> f
                                .rewritePath("/aggregate/accounts-service/v3/api-docs", "/v3/api-docs")
                        )
                        .uri("lb://ACCOUNTS")
                )
                .route(r -> r
                        .path("/aggregate/accounts-service/swagger-ui/**")
                        .filters(f -> f
                                .rewritePath("/aggregate/accounts-service/swagger-ui/(?<segment>.*)", "/swagger-ui/${segment}")
                        )
                        .uri("lb://ACCOUNTS")
                )
                .route(r -> r
                        .path("/webjars/**")
                        .filters(f -> f
                                .rewritePath("/webjars/(?<segment>.*)", "/webjars/${segment}")
                        )
                        .uri("lb://ACCOUNTS")
                )
                .route(p -> p
                        .path("/api/products/**")
                        .filters(f -> f.tokenRelay()
                                .rewritePath("/api/products/(?<segment>.*)", "/${segment}")
//                                .circuitBreaker(c -> c.setName("PRODUCTS-CIRCUIT-BREAKER")
//                                        .setFallbackUri("forward:/productsServiceFallback")
//                                )
                        )
                        .uri("lb://PRODUCTS")
                )
                .route(r -> r
                        .path("/aggregate/products-service/v3/api-docs/**")
                        .filters(f -> f
                                .rewritePath("/aggregate/products-service/v3/api-docs", "/v3/api-docs")
                        )
                        .uri("lb://PRODUCTS")
                )
                .route(p -> p
                        .path("/api/carts/**")
                        .filters(f -> f.tokenRelay()
                                .rewritePath("/api/carts/(?<segment>.*)", "/${segment}")
                                .circuitBreaker(c -> c.setName("CARTS-CIRCUIT-BREAKER")
                                        .setFallbackUri("forward:/cartsServiceFallback")
                                )
                        )
                        .uri("lb://CARTS")
                )
                .route(r -> r
                        .path("/aggregate/carts-service/v3/api-docs/**")
                        .filters(f -> f
                                .rewritePath("/aggregate/carts-service/v3/api-docs", "/v3/api-docs")
                        )
                        .uri("lb://CARTS")
                )
                .route(p -> p
                        .path("/api/orders/**")
                        .filters(f -> f.tokenRelay()
                                .rewritePath("/api/orders/(?<segment>.*)", "/${segment}")
//                                .circuitBreaker(c -> c.setName("ORDERS-CIRCUIT-BREAKER")
//                                        .setFallbackUri("forward:/ordersServiceFallback")
//                                )
                        )
                        .uri("lb://ORDERS")
                )
                .route(r -> r
                        .path("/aggregate/orders-service/v3/api-docs/**")
                        .filters(f -> f
                                .rewritePath("/aggregate/orders-service/v3/api-docs", "/v3/api-docs")
                        )
                        .uri("lb://ORDERS")
                )
                .route(p -> p
                        .path("/api/payment/**")
                        .filters(f -> f.tokenRelay()
                                .rewritePath("/api/payment/(?<segment>.*)", "/${segment}")
                                .circuitBreaker(c -> c.setName("PAYMENT-CIRCUIT-BREAKER")
                                        .setFallbackUri("forward:/paymentServiceFallback")
                                )
                        )
                        .uri("lb://PAYMENT")
                )
                .route(r -> r
                        .path("/aggregate/payment-service/v3/api-docs/**")
                        .filters(f -> f
                                .rewritePath("/aggregate/payment-service/v3/api-docs", "/v3/api-docs")
                        )
                        .uri("lb://PAYMENT")
                )
                .route(p -> p
                        .path("/api/inventory/**")
                        .filters(f -> f.tokenRelay()
                                .rewritePath("/api/inventory/(?<segment>.*)", "/${segment}")
                                .circuitBreaker(c -> c.setName("INVENTORY-CIRCUIT-BREAKER")
                                        .setFallbackUri("forward:/inventoryServiceFallback")
                                )
                        )
                        .uri("lb://INVENTORY")
                )
                .route(r -> r
                        .path("/aggregate/inventory-service/v3/api-docs/**")
                        .filters(f -> f
                                .rewritePath("/aggregate/inventory-service/v3/api-docs", "/v3/api-docs")
                        )
                        .uri("lb://INVENTORY")
                )
                .route(p -> p
                        .path("/api/rates/**")
                        .filters(f -> f.tokenRelay()
                                .rewritePath("/api/rates/(?<segment>.*)", "/${segment}")
//                                .circuitBreaker(c -> c.setName("RATES-CIRCUIT-BREAKER")
//                                        .setFallbackUri("forward:/ratesServiceFallback")
//                                )
                        )
                        .uri("lb://RATES")
                )
                .route(r -> r
                        .path("/aggregate/rates-service/v3/api-docs/**")
                        .filters(f -> f
                                .rewritePath("/aggregate/rates-service/v3/api-docs", "/v3/api-docs")
                        )
                        .uri("lb://RATES")
                )
                .route(p -> p
                        .path("/api/social/**")
                        .filters(f -> f.tokenRelay()
                                .rewritePath("/api/social/(?<segment>.*)", "/${segment}")
                        )
                        .uri("lb://SOCIAL")
                )
                .route(r -> r
                        .path("/aggregate/social-service/v3/api-docs/**")
                        .filters(f -> f
                                .rewritePath("/aggregate/social-service/v3/api-docs", "/v3/api-docs")
                        )
                        .uri("lb://SOCIAL")
                )
                .route(p -> p
                        .path("/api/search/**")
                        .filters(f -> f.tokenRelay()
                                .rewritePath("/api/search/(?<segment>.*)", "/${segment}")
                                .circuitBreaker(c -> c.setName("SEARCH-CIRCUIT-BREAKER")
                                        .setFallbackUri("forward:/searchServiceFallback")
                                )
                        )
                        .uri("lb://SEARCH")
                )
                .route(r -> r
                        .path("/aggregate/search-service/v3/api-docs/**")
                        .filters(f -> f
                                .rewritePath("/aggregate/search-service/v3/api-docs", "/v3/api-docs")
                        )
                        .uri("lb://SEARCH")
                )
                .build();
    }

    @Bean
    public Customizer<ReactiveResilience4JCircuitBreakerFactory> defaultCustomizer() {
        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
                .circuitBreakerConfig(CircuitBreakerConfig.ofDefaults())
                .timeLimiterConfig(TimeLimiterConfig.custom().timeoutDuration(Duration.ofSeconds(10))
                        .build()).build());
    }

    @Bean
    public RedisRateLimiter redisRateLimiter() {
        return new RedisRateLimiter(1, 1, 1);
    }

    @Bean
    KeyResolver userKeyResolver() {
        return exchange -> Mono.justOrEmpty(exchange.getRequest().getHeaders().getFirst("user"))
                .defaultIfEmpty("anonymous");
    }

}
