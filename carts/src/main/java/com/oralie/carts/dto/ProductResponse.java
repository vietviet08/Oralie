package com.oralie.carts.dto;

import com.oralie.carts.dto.response.client.BrandResponse;
import com.oralie.carts.dto.response.client.ProductCategoryResponse;
import com.oralie.carts.dto.response.client.ProductImageResponse;
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
public class ProductResponse {
    private Long id;

    private String name;

    private String slug;

    private String description;

    private String sku;

    private List<ProductCategoryResponse> productCategories;

    private BrandResponse brand;

    private List<ProductImageResponse> images;

    private List<ProductOptionResponse> options;

    private Double price;

    private Boolean isDiscounted;

    private Double discount;

    private Long quantity;

    private Boolean isAvailable;

    private Boolean isDeleted;

    private Boolean isFeatured;

    private Boolean isPromoted;
}
