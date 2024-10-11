package com.oralie.orders.dto.response.client;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartResponse {
    private Long id;
    private String userId;
    private Integer quantity;
    private Double totalPrice;
}
