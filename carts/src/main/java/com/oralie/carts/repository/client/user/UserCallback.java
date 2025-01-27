package com.oralie.carts.repository.client.user;

import com.oralie.carts.dto.client.accounts.UserResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class UserCallback implements UserFeignClient{
    @Override
    public ResponseEntity<UserResponse> getAccountByUsername(String username) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(UserResponse.builder()
                        .username(username)
                        .fullName("Default account")
                        .email("This is a fallback account due to service unavailability.")
                        .build());
    }

    @Override
    public ResponseEntity<UserResponse> getAccountByUserId(String userId) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(UserResponse.builder()
                        .fullName(String.format("Default account, service unavailable get account with userId {} fall", userId ))
                        .email("This is a fallback account due to service unavailability.")
                        .build());
    }
}
