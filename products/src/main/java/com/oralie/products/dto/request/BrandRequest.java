package com.oralie.products.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

@Schema(name = "Brand Request", description = "Schema define the parameters of brand to request")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BrandRequest {
    @NotBlank(message = "Name is required")
    @Size(min = 4, max = 50, message = "Name must be between 4 and 100 characters")
    private String name;

    @Size(min = 4, max = 1000, message = "Description must be between 4 and 1000 characters")
    private String description;

    private MultipartFile image;

    @NotBlank(message = "isActive is required")
    private Boolean isActive;
}
