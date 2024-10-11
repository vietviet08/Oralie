package com.oralie.orders.repository.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "products", fallback = CartFeignClient.class)
public interface CartFeignClient {

}
