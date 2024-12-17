package com.oralie.orders.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PayPalInfoRequest {
    private Double total;

    private String currency;

    private String method;

    private String intent;

    private String description;

    private String cancelUrl;

    private String successUrl;
}
