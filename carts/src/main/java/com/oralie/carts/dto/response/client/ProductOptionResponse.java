package com.oralie.carts.dto.response.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductOptionResponse {
    private Long id;
    private String name;
    private String value;
}