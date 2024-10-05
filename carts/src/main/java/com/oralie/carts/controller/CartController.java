package com.oralie.carts.controller;

import com.oralie.carts.dto.CartContactDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(consumes = "application/json", produces = "application/json")
public class CartController {

    private final Environment environment;

    @Value("${info.app.version}")
    private String build;

    private final CartContactDto cartContactDto;

    @GetMapping("/carts/build-version")
    public ResponseEntity<String> getBuildVersion() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(build);
    }

    @GetMapping("/carts/java-version")
    public ResponseEntity<String> getJavaVersion() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(environment.getProperty("JAVA_HOME"));
    }


    @GetMapping("/carts/contact-info")
    public ResponseEntity<CartContactDto> getProductsContactDto() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(cartContactDto);
    }

}
