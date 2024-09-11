package com.oralie.accounts.controller;

import com.oralie.accounts.constant.AccountConstant;
import com.oralie.accounts.dto.AccountsContactDto;
import com.oralie.accounts.dto.entity.request.AccountRequest;
import com.oralie.accounts.dto.entity.response.AccountResponse;
import com.oralie.accounts.dto.entity.response.ResponseDto;
import com.oralie.accounts.service.AccountService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "CRUD REST APIs for Accounts",
        description = "CREATE, READ, UPDATE, DELETE Accounts"
)
@RestController
@RequestMapping(path = "/accounts", produces = {MediaType.APPLICATION_JSON_VALUE})
public class AccountController {

    @Autowired
    private Environment environment;

    @Autowired
    private AccountsContactDto accountsContactDto;

    @Value("${info.app.version}")
    private String build;

    @Autowired
    private AccountService accountService;

    @PostMapping("/register")
    private ResponseEntity<ResponseDto<?>> registerAccount(@RequestBody @Valid AccountRequest accountRequest) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ResponseDto.builder()
                        .statusMessage(AccountConstant.ACCOUNT_CREATED)
                        .statusCode(HttpStatus.OK.toString())
                        .data(accountService.createAccount(accountRequest))
                        .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> getAccountById(@PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(accountService.getAccountById(id));
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<AccountResponse> getAccountByUsername(@PathVariable String username) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(accountService.getAccount(username));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AccountResponse>> getAllAccounts(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sort
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(accountService.getAccounts(page, size, sortBy, sort));
    }

    @PutMapping("/update")
    public ResponseEntity<ResponseDto<?>> updateAccount(@RequestBody @Valid AccountRequest accountRequest) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseDto.builder()
                        .statusMessage(AccountConstant.ACCOUNT_UPDATED)
                        .statusCode(HttpStatus.OK.toString())
                        .data(accountService.updateAccount(accountRequest))
                        .build());
    }

    @PutMapping("/change-password")
    public ResponseEntity<ResponseDto<?>> changePassword(@RequestParam String username, @RequestParam String password) {
        accountService.changePassword(username, password);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseDto.builder()
                        .statusMessage(AccountConstant.PASSWORD_CHANGED)
                        .statusCode(HttpStatus.OK.toString())
                        .build());
    }

    @DeleteMapping("/delete/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDto<?>> deleteAccount(@PathVariable String username) {
        accountService.deleteAccount(username);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseDto.builder()
                        .statusMessage(AccountConstant.ACCOUNT_DELETED)
                        .statusCode(HttpStatus.OK.toString())
                        .build());
    }

    @GetMapping("/build-version")
    public ResponseEntity<String> getBuildVersion() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(build);
    }

    @GetMapping("/java-version")
    public ResponseEntity<String> getJavaVersion() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(environment.getProperty("JAVA_HOME"));
    }


    @GetMapping("/contact-info")
    public ResponseEntity<AccountsContactDto> getAccountsContactDto() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(accountsContactDto);
    }
}
