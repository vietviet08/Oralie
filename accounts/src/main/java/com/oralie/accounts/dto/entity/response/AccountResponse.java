package com.oralie.accounts.dto.entity.response;

import com.oralie.accounts.dto.identity.Credential;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountResponse {
    private String id;
    private String username;
    private String email;
    private String fullName;
    private String firstName;
    private String lastName;
    private boolean gender;
    private UserAttributeResponse attributes;
    private boolean enabled;
    private boolean emailVerified;
    private List<String> realmRoles;
    private List<Credential> credentials;
}
