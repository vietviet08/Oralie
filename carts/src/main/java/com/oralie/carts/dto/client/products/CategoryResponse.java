package com.oralie.carts.dto.client.products;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryResponse {
    private Long id;

    private String name;

    private String slug;

    private String description;

    private String image;

    private Long parentId;

    private Boolean isDeleted;
}