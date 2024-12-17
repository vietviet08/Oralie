package com.oralie.orders.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderRequest {

    @NotNull
    private OrderAddressRequest address;

    @NotNull
    private List<OrderItemRequest> orderItems;

    @NotNull
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
