package com.oralie.products.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductResponseES {
    @Id
    private Long id;

    private String productName;

    private String slug;

    private List<String> categories;

    private List<String> options;

    private String brand;

    private String sku;

    private String description;

    private Double price;

    private Double discount;

    private Long quantity;

    private Boolean isDiscounted;

    private Boolean isAvailable;

    private Boolean isDeleted;

    private Boolean isFeatured;

    private Boolean isPromoted;
}
