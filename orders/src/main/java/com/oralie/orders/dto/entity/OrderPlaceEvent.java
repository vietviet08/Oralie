package com.oralie.orders.dto.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private Double totalPrice;
}
