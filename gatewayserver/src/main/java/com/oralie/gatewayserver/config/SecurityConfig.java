package com.oralie.gatewayserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import reactor.core.publisher.Mono;

import java.util.Collections;

/**
 * Configuration class for setting up security using Spring WebFlux Security.
 * This class defines various security policies, including allowed API endpoints,
 * roles required for accessing endpoints, and CORS configurations.
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private final String[] freeResourceUrls = {
            "/swagger-ui.html",
            "/swagger-ui/index.html",
            "/webjars/swagger-ui/index.html",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/api-docs-resources/**",
            "/api-docs/**",
            "/aggregate/**",
            "/actuator/**"
    };

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) throws Exception {
        http.csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeExchange(authorizeExchangeSpec -> authorizeExchangeSpec
                        .pathMatchers(freeResourceUrls).permitAll()

                        // Info services
                        .pathMatchers(HttpMethod.GET, "/api/accounts/accounts/**").permitAll()
                        .pathMatchers(HttpMethod.GET, "/api/products/products/**").permitAll()
                        .pathMatchers(HttpMethod.GET, "/api/carts/carts/**").permitAll()
                        .pathMatchers(HttpMethod.GET, "/api/orders/orders/**").permitAll()
                        .pathMatchers(HttpMethod.GET, "/api/payment/payment/**").permitAll()
                        .pathMatchers(HttpMethod.GET, "/api/inventory/inventory/**").permitAll()
                        .pathMatchers(HttpMethod.GET, "/api/rates/rates/**").permitAll()

                        // Store permit all
                        .pathMatchers(HttpMethod.GET, "/api/products/store/**").permitAll()
                        .pathMatchers(HttpMethod.GET, "/api/carts/store/**").permitAll()
                        .pathMatchers(HttpMethod.GET, "/api/social/store/**").permitAll()

                        // Store has role
                        .pathMatchers(HttpMethod.GET, "/api/accounts/store/**").hasRole("CUSTOMER")
                        .pathMatchers(HttpMethod.POST, "/api/accounts/store/**").hasRole("CUSTOMER")
                        .pathMatchers(HttpMethod.PUT, "/api/accounts/store/**").hasRole("CUSTOMER")
                        .pathMatchers(HttpMethod.DELETE, "/api/accounts/store/**").hasRole("CUSTOMER")

                        .pathMatchers(HttpMethod.GET, "/api/carts/store/**").hasRole("CUSTOMER")
                        .pathMatchers(HttpMethod.POST, "/api/carts/store/**").hasRole("CUSTOMER")
                        .pathMatchers(HttpMethod.PUT, "/api/carts/store/**").hasRole("CUSTOMER")
                        .pathMatchers(HttpMethod.DELETE, "/api/carts/store/**").hasRole("CUSTOMER")

                        .pathMatchers(HttpMethod.GET, "/api/orders/store/**").hasRole("CUSTOMER")
                        .pathMatchers(HttpMethod.POST, "/api/orders/store/**").hasRole("CUSTOMER")
                        .pathMatchers(HttpMethod.PUT, "/api/orders/store/**").hasRole("CUSTOMER")

                        .pathMatchers(HttpMethod.GET, "/api/payment/store/**").hasRole("CUSTOMER")
                        .pathMatchers(HttpMethod.POST, "/api/payment/store/**").hasRole("CUSTOMER")
                        .pathMatchers(HttpMethod.PUT, "/api/payment/store/**").hasRole("CUSTOMER")

                        // Dash
                        .pathMatchers(HttpMethod.GET, "/api/products/dash/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.POST, "/api/products/dash/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.PUT, "/api/products/dash/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.DELETE, "/api/products/dash/**").hasRole("ADMIN")

                        .pathMatchers(HttpMethod.GET, "/api/accounts/dash/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.POST, "/api/accounts/dash/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.PUT, "/api/accounts/dash/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.DELETE, "/api/accounts/dash/**").hasRole("ADMIN")

                        .pathMatchers(HttpMethod.GET, "/api/orders/dash/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.POST, "/api/orders/dash/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.PUT, "/api/orders/dash/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.DELETE, "/api/orders/dash/**").hasRole("ADMIN")

                        .pathMatchers(HttpMethod.GET, "/api/carts/dash/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.POST, "/api/carts/dash/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.PUT, "/api/carts/dash/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.DELETE, "/api/carts/dash/**").hasRole("ADMIN")

                        .pathMatchers(HttpMethod.GET, "/api/payment/dash/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.POST, "/api/payment/dash/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.PUT, "/api/payment/dash/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.DELETE, "/api/payment/dash/**").hasRole("ADMIN")

                        .pathMatchers(HttpMethod.GET, "/api/social/dash/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.POST, "/api/social/dash/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.PUT, "/api/social/dash/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.DELETE, "/api/social/dash/**").hasRole("ADMIN")

                        .pathMatchers(HttpMethod.GET, "/api/inventory/dash/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.POST, "/api/inventory/dash/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.PUT, "/api/inventory/dash/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.DELETE, "/api/inventory/dash/**").hasRole("ADMIN")

                        .pathMatchers(HttpMethod.GET, "/api/rates/dash/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.POST, "/api/rates/dash/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.PUT, "/api/rates/dash/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.DELETE, "/api/rates/dash/**").hasRole("ADMIN")

                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2ResourceServerSpec -> oauth2ResourceServerSpec
                        .jwt(jwtSpec -> jwtSpec.jwtAuthenticationConverter(converter()))
                );
        return http.build();
    }

    private Converter<Jwt, Mono<AbstractAuthenticationToken>> converter() {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new KeyCloakRoleConverter());
        return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.applyPermitDefaultValues();
        corsConfig.setAllowedOrigins(Collections.singletonList("*"));
        corsConfig.setAllowedMethods(Collections.singletonList("*"));
        corsConfig.setAllowedHeaders(Collections.singletonList("*"));

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);
        return source;
    }
}