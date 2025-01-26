package com.oralie.accounts.service;

import com.oralie.accounts.dto.entity.response.AccountResponse;

public interface KeycloakAccountService {
    AccountResponse getUserByUserId(String userId);
}
