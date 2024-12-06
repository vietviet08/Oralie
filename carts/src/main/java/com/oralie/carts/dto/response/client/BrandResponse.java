package com.oralie.carts.dto.response.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
