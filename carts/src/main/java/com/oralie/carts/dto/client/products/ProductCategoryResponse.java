package com.oralie.carts.dto.client.products;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductCategoryResponse {

    private Long id;

    private String name;

    private Long idProduct;

    private CategoryResponse category;
}
