package com.oralie.orders.dto.response.paypal;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PaymentResponse {
    private String id;
    private String intent;
    private PayerResponse payer;
    private RedirectUrlsResponse redirectUrls;
    private List<TransactionResponse> transactions;
}