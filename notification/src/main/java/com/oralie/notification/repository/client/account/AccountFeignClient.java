package com.oralie.notification.repository.client.account;

import com.oralie.notification.dto.AccountResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "accounts", fallback = AccountFallBack.class)
public interface AccountFeignClient {

    @GetMapping("/store/accounts/profile")
    public ResponseEntity<AccountResponse> getAccountProfile();
}
