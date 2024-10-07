package com.oralie.carts.dto;

import com.oralie.carts.dto.response.client.UserAddressDto;
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
