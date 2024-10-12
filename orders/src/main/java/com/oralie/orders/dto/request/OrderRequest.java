package com.oralie.orders.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderRequest {
    private OrderAddressRequest address;
    private List<OrderItemRequest> orderItems;
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
