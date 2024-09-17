package com.oralie.products.dto.response;

import com.oralie.products.model.Brand;
import com.oralie.products.model.ProductCategory;
import com.oralie.products.model.ProductImage;
import com.oralie.products.model.ProductOption;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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

    private List<ProductCategory> productCategories;

    private Brand brand;

    private List<ProductImage> images;

    private List<ProductOption> options;

    private Double price;

    private Boolean isDiscounted;

    private Double discount;

    private Integer quantity;

    private String image;

    private Boolean isAvailable;

    private Boolean isDeleted;

    private Boolean isFeatured;

    private Boolean isPromoted;
}
