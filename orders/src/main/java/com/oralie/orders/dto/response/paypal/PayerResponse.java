package com.oralie.orders.dto.response.paypal;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PayerResponse{
    private String paymentMethod;
}