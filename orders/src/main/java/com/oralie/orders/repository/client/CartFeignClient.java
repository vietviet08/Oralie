package com.oralie.orders.repository.client;

import com.oralie.orders.dto.response.client.CartResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name = "carts", fallback = CartFeignClient.class)
public interface CartFeignClient {

    @PutMapping(value = "/store/carts/clear", consumes = "application/json")
    public ResponseEntity<CartResponse> clearCart();

}
