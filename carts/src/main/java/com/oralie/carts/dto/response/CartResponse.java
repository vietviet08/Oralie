package com.oralie.carts.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartResponse {
    private Long id;
    private String userId;
    private Integer quantity;
    private Double totalPrice;
    private Set<CartItemResponse> cartItemResponses;
}
