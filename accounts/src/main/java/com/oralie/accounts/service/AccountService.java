package com.oralie.accounts.service;

import com.oralie.accounts.dto.entity.request.AccountRequest;
import com.oralie.accounts.dto.entity.response.AccountResponse;

import java.util.List;

public interface AccountService {
    AccountResponse createAccount(AccountRequest accountRequest);
    AccountResponse updateAccount(AccountRequest accountRequest);
    void deleteAccount(String username);
    AccountResponse getAccountById(Long id);
    AccountResponse getAccount(String username);
    List<AccountResponse> getAccounts(int page, int size, String sortBy, String sort);
    void changePassword(String username, String password);
}
