package com.oralie.accounts.dto.entity.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountResponse {
    private String username;
    private String email;
    private String phone;
    private String address;
    private String fullName;
    private boolean gender;
}
