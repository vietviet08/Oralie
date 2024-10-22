package com.oralie.notification.repository.client.order;

import com.oralie.notification.dto.OrderResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Component
public class OrderFallBack implements OrderFeignClient {

    @GetMapping("/store/orders/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable("orderId") Long orderId ){
        return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
    };

}
