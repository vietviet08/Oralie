package com.oralie.accounts.service;

import com.oralie.accounts.dto.entity.request.AccountKeyCloakRequest;
import com.oralie.accounts.dto.entity.request.AccountRequest;
import com.oralie.accounts.dto.entity.response.AccountResponse;
import com.oralie.accounts.dto.entity.response.ListResponse;
import com.oralie.accounts.dto.identity.AssignRole;

public interface AccountService {
    AccountResponse createAccount(AccountKeyCloakRequest accountRequest);
    AccountResponse createAccount(AccountRequest accountRequest);
    AccountResponse updateAccount(AccountRequest accountRequest, boolean isCustomer);
    void deleteAccount(String username);
    AssignRole assignRole(String username, String roleName);
    AccountResponse getAccountById(Long id);

    AccountResponse getAccountByUsername(String username);
    AccountResponse getAccountByUserId(String userId);
    ListResponse<AccountResponse> getAccounts(int page, int size, String sortBy, String sort);
    void changePassword(String username, String password);
    AccountResponse getAccountProfile();
    void changePasswordProfile(String password);
    void lockAccount(String username);
    
    boolean existingAccountByUserId(String userId);
}
