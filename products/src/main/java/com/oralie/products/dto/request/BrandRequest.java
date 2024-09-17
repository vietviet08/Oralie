package com.oralie.products.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BrandRequest {
    @NotBlank(message = "Name is required")
    private String name;

    @Size(min = 4, max = 1000, message = "Description must be between 4 and 1000 characters")
    private String description;
    private String urlImage;

    @NotBlank(message = "isActive is required")
    private Boolean isActive;
}
