package com.oralie.gatewayserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import reactor.core.publisher.Mono;

import java.util.List;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private final String[] freeResourceUrls = {"/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**",
            "/swagger-resources/**", "/api-docs/**", "/aggregate/**", "/actuator/**"};

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) throws Exception {
        http.authorizeExchange(authorizeExchangeSpec -> authorizeExchangeSpec
                        .pathMatchers(freeResourceUrls).permitAll()
                        .pathMatchers(HttpMethod.GET, "api/accounts/accounts/**").permitAll()
                        .pathMatchers(HttpMethod.GET, "api/products/products/**").permitAll()
                        .pathMatchers(HttpMethod.GET, "api/carts/carts/**").permitAll()
                        .pathMatchers(HttpMethod.GET, "api/orders/orders/**").permitAll()
                        .pathMatchers(HttpMethod.GET, "api/payment/payment/**").permitAll()
                        .pathMatchers(HttpMethod.GET, "api/inventory/inventory/**").permitAll()
                        .pathMatchers(HttpMethod.GET, "api/rates/rates/**").permitAll()

                        .pathMatchers(HttpMethod.GET, "api/accounts/store/**").permitAll()
                        .pathMatchers(HttpMethod.POST, "api/accounts/store/**").permitAll()
                        .pathMatchers(HttpMethod.GET, "api/products/store/**").permitAll()
                        .pathMatchers(HttpMethod.POST, "api/products/store/**").permitAll()

                        //author by http method here

                        //accounts
                        .pathMatchers(HttpMethod.GET, "api/accounts/dash/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.POST, "api/accounts/dash/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.PUT, "api/accounts/dash/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.DELETE, "api/accounts/dash/**").hasRole("ADMIN")

                        //products
                        .pathMatchers(HttpMethod.GET, "api/products/dash/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.POST, "api/products/dash/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.PUT, "api/products/dash/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.DELETE, "api/products/dash/**").hasRole("ADMIN")

                        //carts
                        .pathMatchers(HttpMethod.GET, "api/carts/dash/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.POST, "api/carts/dash/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.PUT, "api/carts/dash/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.DELETE, "api/carts/dash/**").hasRole("ADMIN")

                        //orders
                        .pathMatchers(HttpMethod.GET, "api/orders/dash/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.POST, "api/orders/dash/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.PUT, "api/orders/dash/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.DELETE, "api/orders/dash/**").hasRole("ADMIN")


                        //payment
                        .pathMatchers(HttpMethod.GET, "api/payment/dash/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.POST, "api/payment/dash/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.PUT, "api/payment/dash/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.DELETE, "api/payment/dash/**").hasRole("ADMIN")

                        //inventory
                        .pathMatchers(HttpMethod.GET, "api/inventory/dash/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.POST, "api/inventory/dash/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.PUT, "api/inventory/dash/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.DELETE, "api/inventory/dash/**").hasRole("ADMIN")

                        //rates
                        .pathMatchers(HttpMethod.GET, "api/rates/dash/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.POST, "api/rates/dash/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.PUT, "api/rates/dash/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.DELETE, "api/rates/dash/**").hasRole("ADMIN")


                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2ResourceServerSpec -> oauth2ResourceServerSpec
                        .jwt(jwtSpec -> jwtSpec.jwtAuthenticationConverter(converter())));
        http.csrf(ServerHttpSecurity.CsrfSpec::disable);
        return http.build();
    }


    private Converter<Jwt, Mono<AbstractAuthenticationToken>> converter() {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new KeyCloakRoleConverter());
        return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter);
    }


//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
//        return httpSecurity.authorizeHttpRequests(authorize -> authorize
//                        .requestMatchers(freeResourceUrls)
//                        .permitAll()
//                        .anyRequest().authenticated())
//                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
//                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
//                .build();
//    }
//
//    @Bean
//    CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration configuration = new CorsConfiguration();
//        configuration.applyPermitDefaultValues();
//        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD"));
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", configuration);
//        return source;
//    }


}
