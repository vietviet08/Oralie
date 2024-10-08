package com.oralie.orders.dto.request;

import com.oralie.orders.dto.response.OrderAddressResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderRequest {
    private OrderAddressRequest address;
    private Double totalPrice;
    private String voucher;
    private Double discount;
    private Double shippingFee;
    private String status;
    private String shippingMethod;
    private String paymentMethod;
    private String paymentStatus;
    private String note;
}
