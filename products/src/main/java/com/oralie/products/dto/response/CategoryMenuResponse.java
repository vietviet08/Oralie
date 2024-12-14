package com.oralie.products.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(name = "Category", description = "Schema define the parameters of category to response")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryMenuResponse {
    private Long id;

    private String name;

    private String slug;

    private String image;

    private Long parentId;

    private List<CategoryMenuResponse> subCategories;
}
