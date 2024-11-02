package com.oralie.rates.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;

@ConfigurationProperties(prefix = "rates")
@Getter
@Setter
public class RateContactDto {
    private String message;
    private Map<String, String> contactInfo;
    private List<String> onCallSupport;
}