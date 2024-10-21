package com.oralie.products.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommonTypeCloudinary {
    private String url;
    private String publicId;
    private String name;
    private String type;
}
