package com.oralie.accounts.service;

import com.oralie.accounts.dto.entity.request.AccountRequest;
import com.oralie.accounts.dto.entity.response.AccountResponse;

import java.util.List;

public interface AccountService {
    AccountResponse createAccount(AccountRequest accountRequest);
    void updateAccount(AccountRequest accountRequest);
    void deleteAccount(String username);
    AccountResponse getAccountById(Long id);
    List<AccountResponse> getAllAccounts();
    AccountResponse getAccount(String username);
    List<AccountResponse> getAccounts();
    void changePassword(String username, String password);
}
