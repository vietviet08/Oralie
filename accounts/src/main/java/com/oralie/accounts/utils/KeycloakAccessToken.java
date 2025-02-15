package com.oralie.accounts.utils;

import com.oralie.accounts.dto.identity.TokenExchangeParam;
import com.oralie.accounts.repository.client.IdentityClient;
import org.springframework.beans.factory.annotation.Value;

public class KeycloakAccessToken {



//    @Value("${idp.client.id}")
//    private String clientId;
//
//    @Value("${idp.client.secret}")
//    private String clientSecret;
//
//    public final String getAccessToken(IdentityClient identityClient) {
//        var token = identityClient.exchangeToken(TokenExchangeParam.builder()
//                .grant_type("client_credentials")
//                .client_id(clientId)
//                .client_secret(clientSecret)
//                .scope("openid")
//                .build());
//
//        return token.getAccessToken();
//    }
}
