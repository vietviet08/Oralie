package com.oralie.accounts.repository.client;

import com.oralie.accounts.dto.identity.*;
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

    // get all user /admin/realms/{realm}/users
    @GetMapping(value = "/admin/realms/oralie/users")
    ResponseEntity<?> getUsers(@RequestHeader("authorization") String token);

    // get user profile /admin/realms/{realm}/users/{user-id}
    @GetMapping(value = "/admin/realms/oralie/users/{user-id}")
    ResponseEntity<?> getUser(@RequestHeader("authorization") String token,
                              @PathVariable("user-id") String userid);

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

    @PostMapping(value = "/admin/realms/oralie/users/{user-id}/role-mappings/clients/oralie-account",
                consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> assignRole(@RequestHeader("authorization") String token,
                                 @RequestBody AssignRole param,
                                 @PathVariable("user-id") String userid);

    @PutMapping(value = "/admin/realms/oralie/users/{user-id}/reset-password", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> updatePassword(@RequestHeader("authorization") String token,
                                    @RequestBody Credential param,
                                    @PathVariable("user-id") String userid);

    @PutMapping(value = "/admin/realms/oralie/users/{user-id}/disable-credential-types", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> lockUser(@RequestHeader("authorization") String token,
                               @PathVariable("user-id") String userid);
}
