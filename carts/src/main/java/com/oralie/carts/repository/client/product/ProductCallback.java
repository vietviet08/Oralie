package com.oralie.carts.repository.client.product;

import com.oralie.carts.dto.ProductResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ProductCallback implements ProductFeignClient {

    @Override
    public ResponseEntity<ProductResponse> getProductById(Long id) {
        return null;
    }
}
