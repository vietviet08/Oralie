package com.oralie.products.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductOptionRequest {

    @NotBlank(message = "Name is required")
    @Size(min = 1, max = 120, message = "Name must be between 1 and 120 characters")
    private String name;

    @NotBlank(message = "Value is required")
    @Size(min = 1, max = 120, message = "Value must be between 1 and 120 characters")
    private Set<String> value;
}
