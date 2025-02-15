package com.oralie.carts.repository.client.user;

import com.oralie.carts.dto.client.accounts.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "accounts", fallback = UserCallback.class)
public interface UserFeignClient {

    @GetMapping("/dash/accounts/user/{username}")
    public ResponseEntity<UserResponse> getAccountByUsername(@PathVariable String username) ;

    @GetMapping("/dash/accounts/userId/{userId}")
    public ResponseEntity<UserResponse> getAccountByUserId(@PathVariable String userId);

}
