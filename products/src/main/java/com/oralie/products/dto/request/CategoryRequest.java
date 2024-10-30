package com.oralie.products.dto.request;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryRequest {

    @NotBlank(message = "Name is required")
    private String name;

    private String slug;

    @Size(min = 4, max = 1000, message = "Description must be between 4 and 1000 characters")
    private String description;

    private MultipartFile image;

    private Long parentId;

    private Boolean isDeleted;
}
