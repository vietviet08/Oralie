package com.oralie.notification.service;

import java.util.Map;

public interface ThymeleafService {
    String createContent(String template, Map<String, Object> variable);

}
