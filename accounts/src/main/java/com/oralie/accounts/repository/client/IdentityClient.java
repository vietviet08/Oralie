package com.oralie.accounts.repository.client;

import com.oralie.accounts.dto.identity.Credential;
import com.oralie.accounts.dto.identity.TokenExchangeParam;
import com.oralie.accounts.dto.identity.TokenExchangeResponse;
import com.oralie.accounts.dto.identity.UserCreationParam;
import feign.QueryMap;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "identity-client", url = "${idp.url}")
public interface IdentityClient {
    @PostMapping(
            value = "/realms/oralie/protocol/openid-connect/token",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    TokenExchangeResponse exchangeToken(@QueryMap TokenExchangeParam param);

    @PostMapping(value = "/admin/realms/oralie/users", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> createUser(@RequestHeader("authorization") String token,
                                 @RequestBody UserCreationParam param);

    @PutMapping(value = "/admin/realms/oralie/users/{user-id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> updateUser(@RequestHeader("authorization") String token,
                                 @RequestBody UserCreationParam param,
                                 @PathVariable("user-id") String userid);

    @DeleteMapping(value = "/admin/realms/oralie/users/{user-id}")
    ResponseEntity<?> deleteUser(@RequestHeader("authorization") String token,
                                 @PathVariable("user-id") String userid);

    @PutMapping(value = "/admin/realms/oralie/users/{user-id}/reset-password", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> updatePassword(@RequestHeader("authorization") String token,
                                    @RequestBody Credential param,
                                    @PathVariable("user-id") String userid);

}
