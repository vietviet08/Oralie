package com.oralie.products.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name = "Category", description = "Schema define the parameters of category to response")
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
