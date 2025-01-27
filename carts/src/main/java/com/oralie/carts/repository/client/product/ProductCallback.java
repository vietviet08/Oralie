package com.oralie.carts.repository.client.product;

import com.oralie.carts.dto.client.products.ProductResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ProductCallback implements ProductFeignClient {

    @Override
    public ResponseEntity<ProductResponse> getProductById(Long id) {
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ProductResponse.builder()
                        .id(id)
                        .name("Default product")
                        .description("This is a fallback product due to service unavailability.")
                        .build());
    }
}
