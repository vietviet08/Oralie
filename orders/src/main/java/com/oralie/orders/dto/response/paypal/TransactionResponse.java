package com.oralie.orders.dto.response.paypal;

import lombok.Builder;
import lombok.Data;
import com.paypal.api.payments.Amount;

@Data
@Builder
public class TransactionResponse {
    private String description;
    private Amount amount;
}