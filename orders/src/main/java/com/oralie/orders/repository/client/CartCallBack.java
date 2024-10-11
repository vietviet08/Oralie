package com.oralie.orders.repository.client;

import com.oralie.orders.dto.response.client.CartResponse;
import jakarta.persistence.Column;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class CartCallBack implements CartFeignClient {

    @Override
    public ResponseEntity<CartResponse> clearCart() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(CartResponse.builder()
                        .id(0L)
                        .userId("0")
                        .quantity(0)
                        .totalPrice(0.0)
                        .build());
    }
}
