package com.oralie.rates.dto.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {
    private String id;
    private String username;
    private String email;
    private String fullName;
    private String firstName;
    private String lastName;
    private boolean gender;
    private UserAttribute attributes;
    private boolean enabled;
    private boolean emailVerified;
    private List<String> realmRoles;
}
