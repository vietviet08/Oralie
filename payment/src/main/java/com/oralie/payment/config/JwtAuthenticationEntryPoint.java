package com.oralie.payment.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oralie.payment.constant.PaymentConstant;
import com.oralie.payment.dto.ResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(
            HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException {
        String errorCode = PaymentConstant.UNAUTHORIZED;

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ResponseDto<?> apiResponse = ResponseDto.builder()
                .statusCode(HttpServletResponse.SC_UNAUTHORIZED+"")
                .statusMessage(errorCode)
                .build();

        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
        response.flushBuffer();
    }
}