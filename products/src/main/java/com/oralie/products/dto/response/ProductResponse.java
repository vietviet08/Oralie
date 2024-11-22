package com.oralie.products.dto.response;

import com.oralie.products.model.Brand;
import com.oralie.products.model.ProductCategory;
import com.oralie.products.model.ProductImage;
import com.oralie.products.model.ProductOption;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Schema(name = "Product", description = "Schema define the request parameters for a product")
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

    private List<ProductSpecificationResponse> specifications;

    private Double price;

    private Boolean isDiscounted;

    private Double discount;

    private Long quantity;

    private Boolean isAvailable;

    private Boolean isDeleted;

    private Boolean isFeatured;

    private Boolean isPromoted;
}
