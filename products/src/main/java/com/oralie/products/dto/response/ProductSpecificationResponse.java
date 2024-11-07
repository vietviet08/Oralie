package com.oralie.products.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name = "Brand Request", description = "Schema define the parameters of product specification to response")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductSpecificationResponse {

    private Long id;

    private String name;

    private String value;

}
