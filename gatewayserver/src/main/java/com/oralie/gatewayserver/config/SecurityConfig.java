package com.oralie.gatewayserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.csrf.CookieServerCsrfTokenRepository;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsWebFilter;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private final String[] freeResourceUrls = {"/swagger-ui.html", "/swagger-ui/index.html", "/webjars/swagger-ui/index.html", "/swagger-ui/**", "/v3/api-docs/**",
            "/api-docs-resources/**", "/api-docs/**", "/aggregate/**", "/actuator/**"};

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) throws Exception {
        http.csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(Customizer.withDefaults())
                .authorizeExchange(authorizeExchangeSpec -> authorizeExchangeSpec

                        .pathMatchers(freeResourceUrls).permitAll()

                        .pathMatchers(HttpMethod.GET, "api/accounts/accounts/**").permitAll()
                        .pathMatchers(HttpMethod.GET, "api/products/products/**").permitAll()
                        .pathMatchers(HttpMethod.GET, "api/carts/carts/**").permitAll()
                        .pathMatchers(HttpMethod.GET, "api/orders/orders/**").permitAll()
                        .pathMatchers(HttpMethod.GET, "api/payment/payment/**").permitAll()
                        .pathMatchers(HttpMethod.GET, "api/inventory/inventory/**").permitAll()
                        .pathMatchers(HttpMethod.GET, "api/rates/rates/**").permitAll()

                        //store
                        .pathMatchers(HttpMethod.GET, "api/*/store/**").permitAll()

                        .pathMatchers(HttpMethod.GET, "api/accounts/store/**").hasRole("CUSTOMER")
                        .pathMatchers(HttpMethod.POST, "api/accounts/store/**").hasRole("CUSTOMER")
                        .pathMatchers(HttpMethod.PUT, "api/accounts/store/**").hasRole("CUSTOMER")
                        .pathMatchers(HttpMethod.DELETE, "api/accounts/store/**").hasRole("CUSTOMER")

                        .pathMatchers(HttpMethod.GET, "api/carts/store/**").hasRole("CUSTOMER")
                        .pathMatchers(HttpMethod.POST, "api/carts/store/**").hasRole("CUSTOMER")
                        .pathMatchers(HttpMethod.PUT, "api/carts/store/**").hasRole("CUSTOMER")
                        .pathMatchers(HttpMethod.DELETE, "api/carts/store/**").hasRole("CUSTOMER")

                        //dash
                        .pathMatchers(HttpMethod.GET, "api/*/dash/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.POST, "api/*/dash/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.PUT, "api/*/dash/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.DELETE, "api/*/dash/**").hasRole("ADMIN")

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

}
