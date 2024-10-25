package com.oralie.accounts.dto.entity.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountKeyCloakRequest {
    private String username;
    private String email;
    private String firstName;
    private String lastName;
}
