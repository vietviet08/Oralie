package com.oralie.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderAddressResponse {
    private Long id;

    private String phoneNumber;

    private String email;

    private String city;

    private String addressDetail;
}
