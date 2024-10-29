package com.oralie.social.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;

@ConfigurationProperties(prefix = "social")
@Getter
@Setter
public class SocialContactDto {

    private String message;
    private Map<String, String> contactInfo;
    private List<String> onCallSupport;

}
