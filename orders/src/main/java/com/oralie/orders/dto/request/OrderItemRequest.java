package com.oralie.orders.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItemRequest {
    private Long productId;

    private String productName;

    private String productImage;

    private Long quantity;

    private Double totalPrice;
}
