package com.oralie.orders.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
    @Pattern(
            regexp = "^(\\+62|0)8[1-9][0-9]{6,}$",
            message = "Phone number should be valid"
    )
    private String phoneNumber;

    @Pattern(
            regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$",
            message = "Email should be valid"
    )
    private String email;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "Address detail is required")
    private String addressDetail;
}
