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
                                .rewritePath("/api/accounts/?(?<remaining>.*)", "/${remaining}")
                                .circuitBreaker(c -> c.setName("ACCOUNTS-CIRCUIT-BREAKER")
                                        .setFallbackUri("forward:/accountsServiceFallback")
                                )
                        )

                        .uri("lb://ACCOUNTS")
                )
                .route(r -> r
                        .path("/aggregate/accounts-service/v3/api-docs/**")
                        .filters(f -> f
                                .rewritePath("/aggregate/accounts-service/v3/api-docs/(?<remaining>.*)", "/v3/api-docs/${remaining}")
                        )
                        .uri("lb://ACCOUNTS")
                )
                .route(r -> r
                        .path("/aggregate/accounts-service/swagger-ui/**")
                        .filters(f -> f
                                .rewritePath("/aggregate/accounts-service/swagger-ui/(?<remaining>.*)", "/swagger-ui/${remaining}")
                        )
                        .uri("lb://ACCOUNTS")
                )
                .route(r -> r
                        .path("/webjars/**")
                        .filters(f -> f
                                .rewritePath("/webjars/(?<remaining>.*)", "/webjars/${remaining}")
                        )
                        .uri("lb://ACCOUNTS")  // Replace with your service
                )
                .route(p -> p
                        .path("/api/products/**")
                        .filters(f -> f.tokenRelay()
                                .rewritePath("/api/products/?(?<remaining>.*)", "/${remaining}")
                                .circuitBreaker(c -> c.setName("PRODUCTS-CIRCUIT-BREAKER")
                                        .setFallbackUri("forward:/productsServiceFallback")
                                )
                        )

                        .uri("lb://PRODUCTS")
                )
                .route(r -> r
                        .path("/aggregate/products-service/v3/api-docs/**")
                        .filters(f -> f
                                .rewritePath("/aggregate/products-service/v3/api-docs/(?<remaining>.*)", "/v3/api-docs/${remaining}")
                        )
                        .uri("lb://PRODUCTS")
                )
                .route(p -> p
                        .path("/api/carts/**")
                        .filters(f -> f.tokenRelay()
                                .rewritePath("/api/carts/?(?<remaining>.*)", "/${remaining}")
                                .circuitBreaker(c -> c.setName("CARTS-CIRCUIT-BREAKER")
                                        .setFallbackUri("forward:/cartsServiceFallback")
                                )
                        )

                        .uri("lb://CARTS")
                )
                .route(r -> r
                        .path("/aggregate/carts-service/v3/api-docs/**")
                        .filters(f -> f
                                .rewritePath("/aggregate/carts-service/v3/api-docs/(?<remaining>.*)", "/v3/api-docs/${remaining}")
                        )
                        .uri("lb://CARTS")
                )
                .route(p -> p
                        .path("/api/orders/**")
                        .filters(f -> f.tokenRelay()
                                .rewritePath("/api/orders/?(?<remaining>.*)", "/${remaining}")
                                .circuitBreaker(c -> c.setName("ORDERS-CIRCUIT-BREAKER")
                                        .setFallbackUri("forward:/ordersServiceFallback")
                                )
                        )

                        .uri("lb://ORDERS")
                )
                .route(p -> p
                        .path("/api/payment/**")
                        .filters(f -> f.tokenRelay()
                                .rewritePath("/api/payment/?(?<remaining>.*)", "/${remaining}")
                                .circuitBreaker(c -> c.setName("PAYMENT-CIRCUIT-BREAKER")
                                        .setFallbackUri("forward:/paymentServiceFallback")
                                )
                        )

                        .uri("lb://PAYMENT")
                )
                .route(p -> p
                        .path("/api/inventory/**")
                        .filters(f -> f.tokenRelay()
                                .rewritePath("/api/inventory/?(?<remaining>.*)", "/${remaining}")
                                .circuitBreaker(c -> c.setName("INVENTORY-CIRCUIT-BREAKER")
                                        .setFallbackUri("forward:/inventoryServiceFallback")
                                )
                        )

                        .uri("lb://INVENTORY")
                )
                .route(p -> p
                        .path("/api/rates/**")
                        .filters(f -> f.tokenRelay()
                                .rewritePath("/api/rates/?(?<remaining>.*)", "/${remaining}")
                                .circuitBreaker(c -> c.setName("RATES-CIRCUIT-BREAKER")
                                        .setFallbackUri("forward:/ratesServiceFallback")
                                )
                        )

                        .uri("lb://RATES")
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
