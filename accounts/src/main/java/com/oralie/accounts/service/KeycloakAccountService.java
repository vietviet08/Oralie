package com.oralie.accounts.service;

import com.oralie.accounts.dto.entity.response.AccountResponse;
import com.oralie.accounts.dto.entity.response.ListResponse;
import com.oralie.accounts.dto.identity.KeycloakUser;

public interface KeycloakAccountService {
    ListResponse<AccountResponse> getUsers(int page, int size, String sortBy, String sort, String search);

    AccountResponse createUser(KeycloakUser keycloakUser);

    AccountResponse getUserByUserId(String userId);

    Boolean existingAccountByUserId(String userId);

    AccountResponse updateUserByUserId(String userId, KeycloakUser keycloakUser);

    void lockUserByUserId(String userId);

    void deleteUserByUserId(String userId);

}
