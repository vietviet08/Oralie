package com.oralie.accounts.service.impl;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oralie.accounts.dto.entity.response.AccountResponse;
import com.oralie.accounts.dto.identity.TokenExchangeParam;
import com.oralie.accounts.exception.ErrorNormalizer;
import com.oralie.accounts.repository.client.IdentityClient;
import com.oralie.accounts.service.KeycloakAccountService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class KeycloakAccountServiceImpl implements KeycloakAccountService {

    private final IdentityClient identityClient;
    private final ErrorNormalizer errorNormalizer;
    private final ObjectMapper objectMapper;

    @Value("${idp.client.id}")
    private String clientId;

    @Value("${idp.client.secret}")
    private String clientSecret;

    @Override
    public AccountResponse getUserByUserId(String userId) {
        try {
            String accessToken = getAccessToken();
            var responseEntity = identityClient.getUser(
                    "Bearer " + accessToken, userId);
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return objectMapper.convertValue(responseEntity.getBody(), AccountResponse.class);
        } catch (FeignException exception) {
            log.error("Error while creating account", exception);
            throw errorNormalizer.handleKeyCloakException(exception);
        }
    }

    private String getAccessToken() {
        var token = identityClient.exchangeToken(TokenExchangeParam.builder()
                .grant_type("client_credentials")
                .client_id(clientId)
                .client_secret(clientSecret)
                .scope("openid")
                .build());
        return token.getAccessToken();
    }
}
