package com.oralie.accounts.service;

import com.oralie.accounts.dto.entity.request.AccountRequest;
import com.oralie.accounts.dto.entity.response.AccountResponse;
import com.oralie.accounts.dto.identity.AssignRole;

import java.util.List;

public interface AccountService {
    AccountResponse createAccount(AccountRequest accountRequest);
    AccountResponse updateAccount(AccountRequest accountRequest, boolean isCustomer);
    void deleteAccount(String username);
    AssignRole assignRole(String username, String roleName);
    AccountResponse getAccountById(Long id);
    AccountResponse getAccount(String username);
    AccountResponse getAccountByUserId(String userId);
    List<AccountResponse> getAccounts(int page, int size, String sortBy, String sort);
    void changePassword(String username, String password);
    AccountResponse getAccountProfile();
    void changePasswordProfile(String password);

}
