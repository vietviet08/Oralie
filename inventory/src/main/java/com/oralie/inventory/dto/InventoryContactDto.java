package com.oralie.inventory.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;

@ConfigurationProperties(prefix = "inventory")
@Getter
@Setter
public class InventoryContactDto {
    private String message;
    private Map<String, String> contactInfo;
    private List<String> onCallSupport;
}