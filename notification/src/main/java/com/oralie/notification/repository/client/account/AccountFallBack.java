package com.oralie.notification.repository.client.account;

import com.oralie.notification.dto.AccountResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class AccountFallBack implements AccountFeignClient {
    @Override
    public ResponseEntity<AccountResponse> getAccountProfile() {
        return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
    }
}
