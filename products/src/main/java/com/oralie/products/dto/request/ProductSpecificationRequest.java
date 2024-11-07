package com.oralie.products.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name = "Product Specification Request", description = "Schema define the request parameters for a product specification")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductSpecificationRequest {

    @NotBlank(message = "Name is required")
    @Size(min = 1, max = 120, message = "Name must be between 1 and 120 characters")
    private String name;

    @NotBlank(message = "Value is required")
    @Size(min = 1, max = 120, message = "Value must be between 1 and 120 characters")
    private String value;
}
