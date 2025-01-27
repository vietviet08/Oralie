package com.oralie.carts.dto.client.search;

import com.oralie.carts.dto.client.products.ProductOptionResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductBaseResponse {
    private Long id;
    private String name;
    private List<ProductOptionResponse> options;
    private String image;
    private String slug;
    private String description;
    private Double price;
    private Long quantity;
    private List<Long> category;
    private Long brand;
    private Double discount;
    private Boolean isDiscounted;
    private Boolean isAvailable;
    private Boolean isDeleted;
    private Boolean isFeatured;
    private Boolean isPromoted;
}
