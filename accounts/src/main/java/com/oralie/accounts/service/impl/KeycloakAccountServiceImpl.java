package com.oralie.accounts.service.impl;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oralie.accounts.dto.entity.response.AccountResponse;
import com.oralie.accounts.dto.entity.response.ListResponse;
import com.oralie.accounts.dto.identity.KeycloakUser;
import com.oralie.accounts.dto.identity.TokenExchangeParam;
import com.oralie.accounts.exception.ErrorNormalizer;
import com.oralie.accounts.repository.client.IdentityClient;
import com.oralie.accounts.service.KeycloakAccountService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
    public ListResponse<AccountResponse> getUsers(int page, int size, String sortBy, String sort, String search) {
        try {
            String accessToken = getAccessToken();

            var responseEntity = identityClient.getUsers("Bearer " + accessToken);

            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            List<?> body = (List<?>) responseEntity.getBody();

            List<AccountResponse> users = Objects.requireNonNull(body).stream()
                    .map(item -> objectMapper.convertValue(item, AccountResponse.class))
                    .collect(Collectors.toList());

            int startIndex = page * size;
            int endIndex = Math.min(startIndex + size, users.size());
            List<AccountResponse> paginatedUsers = users.subList(startIndex, endIndex);

            Comparator<AccountResponse> comparator = Comparator.comparing(user -> {
                return switch (sortBy.toLowerCase()) {
                    case "username" -> user.getUsername();
                    case "email" -> user.getEmail();
                    default -> user.getUsername();
                };
            });
            if (sort.equalsIgnoreCase("desc")) {
                comparator = comparator.reversed();
            }
            paginatedUsers.sort(comparator);

            return ListResponse.<AccountResponse>builder()
                    .data(paginatedUsers)
                    .pageNo(page)
                    .pageSize(size)
                    .totalElements(users.size())
                    .totalPages((int) Math.ceil((double) users.size() / size))
                    .isLast(endIndex == users.size())
                    .build();
        } catch (FeignException exception) {
            log.error("Error while fetching users", exception);
            throw errorNormalizer.handleKeyCloakException(exception);
        }
    }

    @Override
    public AccountResponse createUser(KeycloakUser keycloakUser) {
        try {
            String accessToken = getAccessToken();
            var responseEntity = identityClient.createUser("Bearer " + accessToken, keycloakUser);
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return objectMapper.convertValue(responseEntity.getBody(), AccountResponse.class);
        } catch (FeignException exception) {
            log.error("Error while creating account", exception);
            throw errorNormalizer.handleKeyCloakException(exception);
        }
    }

    @Override
    public AccountResponse getUserByUserId(String userId) {
        try {
            String accessToken = getAccessToken();
            var responseEntity = identityClient.getUser("Bearer " + accessToken, userId);
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return objectMapper.convertValue(responseEntity.getBody(), AccountResponse.class);
        } catch (FeignException exception) {
            log.error("Error while creating account", exception);
            throw errorNormalizer.handleKeyCloakException(exception);
        }
    }

    @Override
    public Boolean existingAccountByUserId(String userId) {
        try {
            String accessToken = getAccessToken();
            var responseEntity = identityClient.getUser("Bearer " + accessToken, userId);
            return responseEntity.getStatusCode().is2xxSuccessful();
        } catch (FeignException exception) {
            log.error("Error while creating account", exception);
            throw errorNormalizer.handleKeyCloakException(exception);
        }
    }

    @Override
    public AccountResponse updateUserByUserId(String userId, KeycloakUser keycloakUser) {
        try {
            String accessToken = getAccessToken();
            var responseEntity = identityClient.getUser("Bearer " + accessToken, userId);
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            AccountResponse accountResponse = objectMapper.convertValue(responseEntity.getBody(), AccountResponse.class);

            KeycloakUser ku = KeycloakUser.builder()
                    .username(accountResponse.getUsername())
                    .enabled(accountResponse.isEnabled())
                    .emailVerified(accountResponse.isEmailVerified())
                    .realmRoles(accountResponse.getRealmRoles())
                    .credentials(accountResponse.getCredentials())
                    .email(keycloakUser.getEmail())
                    .firstName(keycloakUser.getFirstName())
                    .lastName(keycloakUser.getLastName())
                    .attributes(keycloakUser.getAttributes())
                    .build();

            var responseEntityUpdated = identityClient.updateUser("Bearer " + accessToken, ku, userId);
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return objectMapper.convertValue(responseEntityUpdated.getBody(), AccountResponse.class);
        } catch (FeignException exception) {
            log.error("Error while creating account", exception);
            throw errorNormalizer.handleKeyCloakException(exception);
        }
    }

    @Override
    public void lockUserByUserId(String userId) {
        try {
            String accessToken = getAccessToken();
            var responseEntity = identityClient.getUser("Bearer " + accessToken, userId);
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            AccountResponse accountResponse = objectMapper.convertValue(responseEntity.getBody(), AccountResponse.class);
            accountResponse.setEnabled(!accountResponse.isEnabled());
            KeycloakUser keycloakUser = objectMapper.convertValue(accountResponse, KeycloakUser.class);
            identityClient.updateUser("Bearer " + accessToken, keycloakUser, userId);
        } catch (FeignException exception) {
            log.error("Error while creating account", exception);
            throw errorNormalizer.handleKeyCloakException(exception);
        }
    }

    @Override
    public void deleteUserByUserId(String userId) {
        try {
            String accessToken = getAccessToken();
            identityClient.deleteUser("Bearer " + accessToken, userId);
        } catch (FeignException exception) {
            log.error("Error while creating account", exception);
            throw errorNormalizer.handleKeyCloakException(exception);
        }
    }

    private String getAccessToken() {
        var token = identityClient.exchangeToken(TokenExchangeParam.builder().grant_type("client_credentials").client_id(clientId).client_secret(clientSecret).scope("openid").build());
        return token.getAccessToken();
    }


}
