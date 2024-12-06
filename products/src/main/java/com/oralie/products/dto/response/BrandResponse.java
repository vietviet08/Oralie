package com.oralie.products.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name = "Brand Response", description = "Schema define the parameters of brand to response")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BrandResponse {
    private Long id;
    private String name;
    private String slug;
    private String description;
    private String image;
    private Boolean isActive;
}
