package com.oralie.accounts.service.impl;

import com.oralie.accounts.dto.UserAddressDto;
import com.oralie.accounts.dto.entity.request.AccountRequest;
import com.oralie.accounts.dto.entity.response.AccountResponse;
import com.oralie.accounts.dto.entity.response.ListResponse;
import com.oralie.accounts.dto.identity.AssignRole;
import com.oralie.accounts.dto.identity.Credential;
import com.oralie.accounts.dto.identity.TokenExchangeParam;
import com.oralie.accounts.dto.identity.UserCreationParam;
import com.oralie.accounts.exception.ErrorNormalizer;
import com.oralie.accounts.exception.ResourceNotFoundException;
import com.oralie.accounts.model.Account;
import com.oralie.accounts.model.UserAddress;
import com.oralie.accounts.repository.AccountsRepository;
import com.oralie.accounts.repository.client.IdentityClient;
import com.oralie.accounts.repository.UserAddressRepository;
import com.oralie.accounts.service.AccountService;
import feign.FeignException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private static final Logger log = LoggerFactory.getLogger(AccountServiceImpl.class);

    private final AccountsRepository accountsRepository;
    private final UserAddressRepository userAddressRepository;
    private final IdentityClient identityClient;
    private final ErrorNormalizer errorNormalizer;

    @Value("${idp.client-id}")
    private String clientId;

    @Value("${idp.client-secret}")
    private String clientSecret;

    // get access token? => all request can accept it?
    private String getAccessToken() {
        var token = identityClient.exchangeToken(TokenExchangeParam.builder()
                .grant_type("client_credentials")
                .client_id(clientId)
                .client_secret(clientSecret)
                .scope("openid")
                .build());

        return token.getAccessToken();
    }

    @Override
    public AccountResponse createAccount(AccountRequest request) {
        try {
            System.out.println(getAccessToken());
            // Get userId of keyCloak account
            var creationResponse = identityClient.createUser(
                    "Bearer " + getAccessToken(),
                    UserCreationParam.builder()
                            .username(request.getUsername())
                            .firstName(request.getFirstName())
                            .lastName(request.getLastName())
                            .email(request.getEmail())
                            .enabled(true)
                            .emailVerified(false)
                            .realmRoles(List.of("CUSTOMER"))
                            .credentials(List.of(Credential.builder()
                                    .type("password")
                                    .temporary(false)
                                    .value(request.getPassword())
                                    .build()))
                            .build());

            String userId = extractUserId(creationResponse);
            log.info("UserId {}", userId);

            var profile = mapToAccount(request);
            profile.setUserId(userId);

            Account account = accountsRepository.save(profile);

            List<UserAddress> userAddresses = new ArrayList<>();
            UserAddress address = UserAddress.builder()
                    .account(account)
                    .userId(userId)
                    .addressDetail(request.getAddressDetail())
                    .phone(request.getPhoneNumber())
                    .city(request.getCity())
                    .build();
            userAddresses.add(address);
            account.setAddress(userAddresses);

            accountsRepository.save(account);

            return mapToAccountResponse(account);

        } catch (FeignException exception) {
            log.error("Error while creating account", exception);

            throw errorNormalizer.handleKeyCloakException(exception);
        }
    }

    @Override
    public AccountResponse updateAccount(AccountRequest accountRequest, boolean isCustomer) {
        try {
            Account account = accountsRepository.findByUsername(accountRequest.getUsername())
                    .orElseThrow(() -> new ResourceNotFoundException("Account not found", "username", accountRequest.getUsername()));
            if (isCustomer) {
                String userIdExisting = SecurityContextHolder.getContext().getAuthentication().getName();
                String userIdCustomer = account.getUserId();
                if (!userIdExisting.equals(userIdCustomer)) {
                    throw new ResourceNotFoundException("Account not found", "username", accountRequest.getUsername());
                }
            }
//            String password = account.getPassword();

            var creationResponse = identityClient.updateUser(
                    "Bearer " + getAccessToken(),
                    UserCreationParam.builder()
//                            .username(account.getUsername())
                            .firstName(accountRequest.getFirstName())
                            .lastName(accountRequest.getLastName())
                            .email(accountRequest.getEmail())
                            .enabled(true)
                            .emailVerified(false)
                            .build(), account.getUserId());

            String userId = extractUserId(creationResponse);

            var profile = mapToAccount(accountRequest);
            profile.setUserId(userId);

            Account accountSave = accountsRepository.save(profile);
            return mapToAccountResponse(accountSave);
        } catch (FeignException exception) {
            log.error("Error while update account", exception);
            throw errorNormalizer.handleKeyCloakException(exception);
        }
    }

    @Override
    @Transactional
    public void deleteAccount(String username) {
        try {
            Account account = accountsRepository.findByUsername(username)
                    .orElseThrow(() -> new ResourceNotFoundException("Account not found", "username", username));
            String userId = account.getUserId();

            var creationResponse = identityClient.deleteUser(
                    "Bearer " + getAccessToken(),
                    userId);

            accountsRepository.deleteById(account.getId());
        } catch (FeignException exception) {
            log.error("Error while delete account", exception);
            throw errorNormalizer.handleKeyCloakException(exception);
        }
    }

    @Override
    public AssignRole assignRole(String username, String roleName) {
        try {
            Account account = accountsRepository.findByUsername(username)
                    .orElseThrow(() -> new ResourceNotFoundException("Account not found", "username", username));

            AssignRole assignRole = AssignRole.builder()
                    .name(username)
                    .description(roleName)
                    .clientRole(true)
                    .build();

            identityClient.assignRole(
                    "Bearer " + getAccessToken(),
                    assignRole,
                    account.getUserId());
            return assignRole;

        } catch (FeignException exception) {
            log.error("Error while assign role", exception);
            throw errorNormalizer.handleKeyCloakException(exception);
        }
    }

    @Override
    public AccountResponse getAccountById(Long id) {
        Account account = accountsRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Account not found", "id", id.toString()));
        return mapToAccountResponse(account);
    }


    @Override
    public AccountResponse getAccount(String username) {
        Account account = accountsRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("Account not found", "username", username));
        return mapToAccountResponse(account);
    }

    @Override
    public AccountResponse getAccountByUserId(String userId) {
        Account account = accountsRepository.findByUserId(userId).orElseThrow(() -> new ResourceNotFoundException("Account not found", "userId", userId));
        return mapToAccountResponse(account);
    }

    @Override
    public ListResponse<AccountResponse> getAccounts(int page, int size, String sortBy, String sort) {

        Sort sortObj = sort.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sortObj);
        Page<Account> pageAccount = accountsRepository.findAll(pageable);
        List<Account> accounts = pageAccount.getContent();

        return ListResponse.<AccountResponse>builder()
                .data(mapToAccountListResponse(accounts))
                .pageNo(pageAccount.getNumber())
                .pageSize(pageAccount.getSize())
                .totalElements((int) pageAccount.getTotalElements())
                .totalPages(pageAccount.getTotalPages())
                .isLast(pageAccount.isLast())
                .build();

    }

    @Override
    public void changePassword(String username, String password) {
        try {
            Account account = accountsRepository.findByUsername(username)
                    .orElseThrow(() -> new ResourceNotFoundException("Account not found", "username", username));

            identityClient.updatePassword(
                    "Bearer " + getAccessToken(),
                    Credential.builder()
                            .type("password")
                            .temporary(false)
                            .value(password)
                            .build(),
                    account.getUserId());
        } catch (FeignException exception) {
            log.error("Error while change password", exception);
            throw errorNormalizer.handleKeyCloakException(exception);
        }
    }

    @Override
    public AccountResponse getAccountProfile() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        Account account = accountsRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found", "userId", userId));
        return mapToAccountResponse(account);
    }

    @Override
    public void changePasswordProfile(String password) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            identityClient.updatePassword(
                    "Bearer " + getAccessToken(),
                    Credential.builder()
                            .type("password")
                            .temporary(false)
                            .value(password)
                            .build(),
                    userId);
        } catch (FeignException exception) {
            log.error("Error while change password", exception);
            throw errorNormalizer.handleKeyCloakException(exception);
        }
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


    private Account mapToAccount(AccountRequest accountRequest) {
        Account account = new Account();
        account.setUsername(accountRequest.getUsername());
        account.setPassword(new BCryptPasswordEncoder().encode(accountRequest.getPassword()));
        account.setEmail(accountRequest.getEmail());
        account.setAddress(null);
        account.setFullName(accountRequest.getFirstName() + " " + accountRequest.getLastName());
        account.setGender(accountRequest.getGender());
        return account;
    }

    private AccountResponse mapToAccountResponse(Account account) {
        return AccountResponse.builder()
                .username(account.getUsername())
                .email(account.getEmail())
                .address(account.getAddress() != null ? mapToListUserAddressDto(account.getAddress()) : null)
                .fullName(account.getFullName())
                .gender(account.getGender())
                .build();
    }

    private List<AccountResponse> mapToAccountListResponse(List<Account> accounts) {
        return accounts.stream()
                .map(account -> AccountResponse.builder()
                        .username(account.getUsername())
                        .email(account.getEmail())
                        .address(account.getAddress() != null ? mapToListUserAddressDto(account.getAddress()) : null)
                        .fullName(account.getFullName())
                        .gender(account.getGender())
                        .build())
                .toList();
    }

    private List<UserAddressDto> mapToListUserAddressDto(List<UserAddress> userAddresses) {
        return userAddresses.stream()
                .map(userAddress -> UserAddressDto.builder()
                        .userId(userAddress.getUserId())
                        .phone(userAddress.getPhone())
                        .city(userAddress.getCity())
                        .addressDetail(userAddress.getAddressDetail())
                        .city(userAddress.getCity()).build())
                .toList();
    }
}
