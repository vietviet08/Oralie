package com.oralie.carts.dto.client.accounts;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserAddressDto {
    private String userId;
    private String username;
    private String phone;
    private String addressDetail;
    private String city;
}