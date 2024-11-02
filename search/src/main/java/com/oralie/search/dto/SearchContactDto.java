package com.oralie.search.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;

@ConfigurationProperties(prefix = "search")
@Getter
@Setter
public class SearchContactDto {
    private String message;
    private Map<String, String> contactInfo;
    private List<String> onCallSupport;
}