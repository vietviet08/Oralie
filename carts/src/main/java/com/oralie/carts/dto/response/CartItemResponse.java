package com.oralie.carts.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartItemResponse {
    private Long id;
    private Long productId;
    private String productName;
    private String urlImageThumbnail;
    private String productSlug;
    private Integer quantity;
    private Double price;
    private Double totalPrice;
}
