package com.oralie.inventory.dto.event;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItemEvent {
    private Long productId;
    private String productName;
    private String productImage;
    private Long quantity;
    private Double totalPrice;
    private boolean isRated = false;
}
