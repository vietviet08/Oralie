package com.oralie.accounts.controller;

import com.oralie.accounts.dto.entity.response.AccountResponse;
import com.oralie.accounts.dto.entity.response.ListResponse;
import com.oralie.accounts.dto.identity.KeycloakUser;
import com.oralie.accounts.service.KeycloakAccountService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "KEYCLOAK REST APIs for Accounts",
        description = "Interact with accounts in Keycloak"
)
@RestController
@RequestMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
@RequiredArgsConstructor
public class KeycloakAccountController {

    private final KeycloakAccountService keycloakAccountService;

    @GetMapping("/dash/keycloak/users")
    public ResponseEntity<ListResponse<AccountResponse>> getUsers(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "username") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sort,
            @RequestParam(required = false) String search
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(keycloakAccountService.getUsers(page, size, sortBy, sort, search));
    }

    @PostMapping("/dash/keycloak/users")
    public ResponseEntity<AccountResponse> createUser(@RequestBody KeycloakUser keycloakUser) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(keycloakAccountService.createUser(keycloakUser));
    }

    @GetMapping("/store/keycloak/users/{userId}")
    public ResponseEntity<AccountResponse> getUserByUserId(@PathVariable String userId) {
        String userIdContext = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!userId.equals(userIdContext))
            throw new RuntimeException("You are not authorized to access this resource");
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(keycloakAccountService.getUserByUserId(userId));
    }

    @PutMapping("/store/keycloak/users/{userId}")
    public ResponseEntity<AccountResponse> updateUserByUserId(@PathVariable String userId,
                                                              @RequestBody KeycloakUser keycloakUser) {
        String userIdContext = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!userId.equals(userIdContext))
            throw new RuntimeException("You are not authorized to access this resource");
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(keycloakAccountService.updateUserByUserId(userId, keycloakUser));
    }

    @PutMapping("/dash/keycloak/users/lock/{userId}")
    public ResponseEntity<Void> lockUserByUserId(@PathVariable String userId) {
        keycloakAccountService.lockUserByUserId(userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

    @DeleteMapping("/dash/keycloak/users/{userId}")
    public ResponseEntity<Void> deleteUserByUserId(@PathVariable String userId) {
        keycloakAccountService.deleteUserByUserId(userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

    @GetMapping("/store/keycloak/existing/{userId}")
    public ResponseEntity<Boolean> existingAccountByUserId(@PathVariable String userId) {
        String userIdContext = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!userId.equals(userIdContext))
            throw new RuntimeException("You are not authorized to access this resource");
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(keycloakAccountService.existingAccountByUserId(userId));
    }

}
