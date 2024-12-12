package com.oralie.carts.dto.response;

import com.oralie.carts.dto.response.client.ProductOptionResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartItemResponse {
    private Long id;
    private Long productId;
    private String productName;
    private Long productOptionId;
    private List<ProductOptionResponse> productOptions;
    private String urlImageThumbnail;
    private String productSlug;
    private Integer quantity;
    private Double price;
    private Double totalPrice;
}
