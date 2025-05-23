package com.oralie.carts.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;

@ConfigurationProperties(prefix = "carts")
@Getter
@Setter
public class CartContactDto {
    private String message;
    private Map<String, String> contactInfo;
    private List<String> onCallSupport;
}