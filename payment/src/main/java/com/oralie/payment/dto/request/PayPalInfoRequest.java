package com.oralie.payment.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PayPalInfoRequest {
    Double total;
    String currency;
    String method;
    String intent;
    String description;
    String cancelUrl;
    String successUrl;
}

