package com.oralie.accounts.controller;

import com.oralie.accounts.dto.entity.response.AccountResponse;
import com.oralie.accounts.service.KeycloakAccountService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
        name = "KEYCLOAK REST APIs for Accounts",
        description = "Interact with accounts in Keycloak"
)
@RestController
@RequestMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
@RequiredArgsConstructor
public class KeycloakAccountController {

    private final KeycloakAccountService keycloakAccountService;

    @GetMapping("/store/keycloak/users/{userId}")
    public ResponseEntity<AccountResponse> getUserByUserId(@PathVariable String userId) {
        String userIdContext = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!userId.equals(userIdContext))
            throw new RuntimeException("You are not authorized to access this resource");
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(keycloakAccountService.getUserByUserId(userId));
    }

}
