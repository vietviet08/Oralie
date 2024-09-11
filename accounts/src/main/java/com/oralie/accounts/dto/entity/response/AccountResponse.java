package com.oralie.accounts.dto.entity.response;

import com.oralie.accounts.dto.UserAddressDto;
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
    private String username;
    private String email;
    private List<UserAddressDto> address;
    private String fullName;
    private boolean gender;
}
