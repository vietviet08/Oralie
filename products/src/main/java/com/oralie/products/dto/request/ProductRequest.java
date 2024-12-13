package com.oralie.products.dto.request;

import com.oralie.products.model.Brand;
import com.oralie.products.model.ProductCategory;
import com.oralie.products.model.ProductImage;
import com.oralie.products.model.ProductOption;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductRequest {

    @NotBlank(message = "Name is required")
    @Size(min = 4, max = 100, message = "Name must be between 4 and 100 characters")
    private String name;

    @NotBlank(message = "Slug is required")
    private String slug;

    @Size(min = 4, message = "Description must be right than 4 characters")
    private String description;

    private String sku;

    private List<Long> categoryIds;

    private Long brandId;

    private List<MultipartFile> images;

    private List<ProductOptionRequest> options;

    private List<ProductSpecificationRequest> specifications;

    @NotBlank(message = "Price is required")
    @DecimalMin(value = "0.0", message = "Price must be greater than 0")
    private Double price;

    private Double discount;

    @NotBlank(message = "Quantity is required")
    @DecimalMin(value = "0", message = "Quantity must be greater than 0")
    private Long quantity;

    private Boolean isDiscounted;

    private Boolean isAvailable = true;

    private Boolean isDeleted = false;

    private Boolean isFeatured = true;

    private Boolean isPromoted = true;
}
