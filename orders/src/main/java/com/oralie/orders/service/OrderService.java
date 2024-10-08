package com.oralie.orders.service;

import com.oralie.orders.dto.request.OrderRequest;
import com.oralie.orders.dto.response.ListResponse;
import com.oralie.orders.dto.response.OrderResponse;

public interface OrderService {
    OrderResponse createOrder(OrderRequest orderRequest);
    ListResponse<OrderResponse> getOrdersByUserId(String userId);
    OrderResponse viewOrder(String userid);

}
