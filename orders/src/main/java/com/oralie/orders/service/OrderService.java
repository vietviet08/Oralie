package com.oralie.orders.service;

import com.oralie.orders.dto.request.OrderRequest;
import com.oralie.orders.dto.response.ListResponse;
import com.oralie.orders.dto.response.OrderItemResponse;
import com.oralie.orders.dto.response.OrderResponse;
import com.oralie.orders.exception.PaymentProcessingException;

import java.util.List;

public interface OrderService {

    ListResponse<OrderResponse> getAllOrders(int page, int size, String sortBy, String sort);

    OrderResponse createOrder(OrderRequest orderRequest) throws PaymentProcessingException;

    ListResponse<OrderResponse> getOrdersByUserId(String userId, int page, int size, String sortBy, String sort);

    ListResponse<OrderItemResponse> getOrderItemsByOrderId(Long orderId);

    OrderResponse viewOrder(Long idOrder);

    OrderResponse updateOrderStatus(Long orderId, String status);

    String cancelOrder(Long orderId);

}
