package com.oralie.orders.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;

@ConfigurationProperties(prefix = "carts")
@Getter
@Setter
public class OrderContactDto {
    private String message;
    private Map<String, String> contactInfo;
    private List<String> onCallSupport;
}