package com.oralie.orders.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderResponse {
    private Long id;
    private Long userId;
    private Long cartId;
    private Double totalPrice;
    private String status;
    private String paymentMethod;
    private String paymentStatus;


}
