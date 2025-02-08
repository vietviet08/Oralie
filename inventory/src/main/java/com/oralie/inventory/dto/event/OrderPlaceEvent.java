package com.oralie.inventory.dto.event;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderPlaceEvent {
    private Long orderId;
    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    private List<OrderItemEvent> orderItems;
    private Double totalPrice;
    private Double discount;
    private Double shippingFee;
    private String status;
    private String shippingMethod;
    private String paymentMethod;
    private String tokenViewOrder;
}

