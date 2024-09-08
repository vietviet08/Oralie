package com.oralie.accounts.service.impl;

import com.oralie.accounts.constant.AccountConstant;
import com.oralie.accounts.dto.entity.request.AccountRequest;
import com.oralie.accounts.dto.entity.response.AccountResponse;
import com.oralie.accounts.dto.identity.Credential;
import com.oralie.accounts.dto.identity.TokenExchangeParam;
import com.oralie.accounts.dto.identity.UserCreationParam;
import com.oralie.accounts.exception.AccountAlreadyExistException;
import com.oralie.accounts.model.Account;
import com.oralie.accounts.repository.AccountsRepository;
import com.oralie.accounts.repository.IdentityClient;
import com.oralie.accounts.service.AccountService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private static final Logger log = LoggerFactory.getLogger(AccountServiceImpl.class);

    private final AccountsRepository accountsRepository;
    private final IdentityClient identityClient;

    @Value("${idp.client-id}")
    private String clientId;

    @Value("${idp.client-secret}")
    private String clientSecret;

    @Override
    public String createAccount(AccountRequest request) {
        try {
            var token = identityClient.exchangeToken(TokenExchangeParam.builder()
                    .grant_type("client_credentials")
                    .client_id(clientId)
                    .client_secret(clientSecret)
                    .scope("openid")
                    .build());

            log.info("TokenInfo {}", token);
            // Create user with client Token and given info

            // Get userId of keyCloak account
            var creationResponse = identityClient.createUser(
                    "Bearer " + token.getAccessToken(),
                    UserCreationParam.builder()
                            .username(request.getUsername())
                            .firstName(request.getFirstName())
                            .lastName(request.getLastName())
                            .email(request.getEmail())
                            .enabled(true)
                            .emailVerified(false)
                            .credentials(List.of(Credential.builder()
                                    .type("password")
                                    .temporary(false)
                                    .value(request.getPassword())
                                    .build()))
                            .build());

            String userId = extractUserId(creationResponse);
            log.info("UserId {}", userId);

            var profile = mapAccountRequestToAccount(request);
            profile.setUserId(userId);

             accountsRepository.save(profile);
            return AccountConstant.ACCOUNT_CREATED;
        } catch (FeignException exception) {
           log.error("Error while creating account", exception);
           return AccountConstant.FEIGN_EXCEPTION;
        }
    }

    @Override
    public void updateAccount(AccountRequest accountRequest) {

    }

    @Override
    public void deleteAccount(String username) {

    }

    @Override
    public AccountResponse getAccount(String username) {
        return null;
    }

    @Override
    public List<AccountResponse> getAccounts() {
        return List.of();
    }

    @Override
    public void changePassword(String username, String password) {

    }

   private String extractUserId(ResponseEntity<?> response) {
    List<String> locationHeaders = response.getHeaders().get("Location");
    if (locationHeaders == null || locationHeaders.isEmpty()) {
        throw new IllegalArgumentException("Location header is missing");
    }
    String location = locationHeaders.get(0);
    String[] splitedStr = location.split("/");
    return splitedStr[splitedStr.length - 1];
}

    private Account mapAccountRequestToAccount(AccountRequest accountRequest) {
        Account account = new Account();
        account.setUsername(accountRequest.getUsername());
        account.setEmail(accountRequest.getEmail());
        account.setPhone(accountRequest.getPhone());
        account.setAddress(accountRequest.getAddress());
        account.setFullName(accountRequest.getFirstName() + " " + accountRequest.getLastName());
        account.setGender(accountRequest.getGender());
        return account;
    }
}
