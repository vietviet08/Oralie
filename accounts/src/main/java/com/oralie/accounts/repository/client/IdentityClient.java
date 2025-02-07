package com.oralie.accounts.repository.client;

import com.oralie.accounts.dto.identity.*;
import feign.QueryMap;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "identity-client", url = "${idp.url}")
public interface IdentityClient {
    String REALM = "oralie";

    @PostMapping(
            value = "/realms/" + IdentityClient.REALM + "/protocol/openid-connect/token",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    TokenExchangeResponse exchangeToken(@QueryMap TokenExchangeParam param);

    @GetMapping(value = "/admin/realms/" + IdentityClient.REALM + "/users")
    ResponseEntity<?> getUsers(@RequestHeader("authorization") String token);

    @GetMapping(value = "/admin/realms/" + IdentityClient.REALM + "/users/{user-id}")
    ResponseEntity<?> getUser(@RequestHeader("authorization") String token,
                              @PathVariable("user-id") String userid);

    @PostMapping(value = "/admin/realms/" + IdentityClient.REALM + "/users")
    ResponseEntity<?> createUser(@RequestHeader("authorization") String token,
                                 @RequestBody KeycloakUser user);

    @PutMapping(value = "/admin/realms/" + IdentityClient.REALM + "/users/{user-id}")
    ResponseEntity<?> updateUser(@RequestHeader("authorization") String token,
                                 @RequestBody KeycloakUser user,
                                 @PathVariable("user-id") String userid);

    @DeleteMapping(value = "/admin/realms/" + IdentityClient.REALM + "/users/{user-id}")
    ResponseEntity<?> deleteUser(@RequestHeader("authorization") String token,
                                 @PathVariable("user-id") String userid);

    @PostMapping(value = "/admin/realms/" + IdentityClient.REALM + "/users/{user-id}/role-mappings/clients/${idp.client-id}",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> assignRole(@RequestHeader("authorization") String token,
                                 @RequestBody AssignRole param,
                                 @PathVariable("user-id") String userid);

    @PutMapping(value = "/admin/realms/" + IdentityClient.REALM + "/users/{user-id}/reset-password", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> updatePassword(@RequestHeader("authorization") String token,
                                     @RequestBody Credential param,
                                     @PathVariable("user-id") String userid);

    @PutMapping(value = "/admin/realms/" + IdentityClient.REALM + "/users/{user-id}/disable-credential-types", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> lockUser(@RequestHeader("authorization") String token,
                               @PathVariable("user-id") String userid);
}