package com.oralie.payment.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;

@ConfigurationProperties(prefix = "payment")
@Getter
@Setter
public class PaymentContactDto {
    private String message;
    private Map<String, String> contactInfo;
    private List<String> onCallSupport;
}