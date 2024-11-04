package com.oralie.carts.repository.client.product;

import com.oralie.carts.dto.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "products", fallback = ProductCallback.class)
public interface ProductFeignClient {

    @GetMapping(value = "/store/products/id/{id}", consumes = "application/json")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id);

}
