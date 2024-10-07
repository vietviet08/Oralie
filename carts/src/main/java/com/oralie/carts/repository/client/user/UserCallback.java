package com.oralie.carts.repository.client.user;

import com.oralie.carts.dto.AccountResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class UserCallback implements UserFeignClient{
    @Override
    public ResponseEntity<AccountResponse> getAccountByUsername(String username) {
        return null;
    }

    @Override
    public ResponseEntity<AccountResponse> getAccountByUserId(String userId) {
        return null;
    }
}
