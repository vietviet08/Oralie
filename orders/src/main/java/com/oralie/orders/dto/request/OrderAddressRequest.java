package com.oralie.orders.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderAddressRequest {

    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    private String email;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "Address detail is required")
    private String addressDetail;
}
