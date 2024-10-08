package com.oralie.orders.service.impl;

import com.oralie.orders.dto.request.OrderRequest;
import com.oralie.orders.dto.response.ListResponse;
import com.oralie.orders.dto.response.OrderAddressResponse;
import com.oralie.orders.dto.response.OrderItemResponse;
import com.oralie.orders.dto.response.OrderResponse;
import com.oralie.orders.exception.ResourceNotFoundException;
import com.oralie.orders.model.Order;
import com.oralie.orders.model.OrderAddress;
import com.oralie.orders.model.OrderItem;
import com.oralie.orders.repository.OrderRepository;
import com.oralie.orders.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {


    private final OrderRepository orderRepository;

    @Override
    public ListResponse<OrderResponse> getAllOrders(int page, int size, String sortBy, String sort) {
        Sort sortObj = sort.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sortObj);
        Page<Order> pageOrders = orderRepository.findAll(pageable);
        List<Order> orders = pageOrders.getContent();

        return ListResponse
                .<OrderResponse>builder()
                .data(mapToOrderResponseList(orders))
                .pageNo(pageOrders.getNumber())
                .pageSize(pageOrders.getSize())
                .totalElements((int) pageOrders.getTotalElements())
                .totalPages(pageOrders.getTotalPages())
                .isLast(pageOrders.isLast())
                .build();
    }

    @Override
    public OrderResponse createOrder(OrderRequest orderRequest) {
        return null;
    }

    @Override
    public ListResponse<OrderResponse> getOrdersByUserId(String userId) {
//        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Order> orders = orderRepository.findByUserId(userId);
        return ListResponse
                .<OrderResponse>builder()
                .data(mapToOrderResponseList(orders))
                .build();
    }

    @Override
    public ListResponse<OrderItemResponse> getOrderItemsByOrderId(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found", "id", orderId + ""));
        Set<OrderItem> orderItems = order.getOrderItems();
        return ListResponse
                .<OrderItemResponse>builder()
                .data(mapToOrderItemResponse(orderItems).stream().toList())
                .build();
    }

    @Override
    public OrderResponse viewOrder(Long idOrder) {
        Order order = orderRepository.findById(idOrder)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found", "id", idOrder + ""));
        return mapToOrderResponse(order);
    }

    @Override
    public OrderResponse updateOrderStatus(Long orderId, String status) {
       Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found", "id", orderId + ""));
        order.setStatus(status);
        orderRepository.save(order);
        return mapToOrderResponse(order);
    }

    @Override
    public String cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found", "id", orderId + ""));
        order.setStatus("CANCELLED");
        orderRepository.save(order);
        return "Order cancelled successfully";
    }

    private OrderResponse mapToOrderResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .cartId(order.getCartId())
                .address(mapToOrderAddressResponse(order.getAddress()))
                .orderItems(mapToOrderItemResponse(order.getOrderItems()))
                .totalPrice(order.getTotalPrice())
                .voucher(order.getVoucher())
                .discount(order.getDiscount())
                .shippingFee(order.getShippingFee())
                .status(order.getStatus())
                .shippingMethod(order.getShippingMethod())
                .paymentMethod(order.getPaymentMethod())
                .paymentStatus(order.getPaymentStatus())
                .note(order.getNote())
                .build();
    }

    private Set<OrderItemResponse> mapToOrderItemResponse(Set<OrderItem> orderItems) {
        return orderItems.stream()
                .map(orderItem -> OrderItemResponse.builder()
                        .id(orderItem.getId())
                        .productId(orderItem.getProductId())
                        .productName(orderItem.getProductName())
                        .quantity(orderItem.getQuantity())
                        .totalPrice(orderItem.getTotalPrice())
                        .order(orderItem.getOrder())
                        .build())
                .collect(Collectors.toSet());
    }

    private OrderAddressResponse mapToOrderAddressResponse(OrderAddress address) {
        return OrderAddressResponse.builder()
                .id(address.getId())
                .addressDetail(address.getAddressDetail())
                .city(address.getCity())
                .email(address.getEmail())
                .phoneNumber(address.getPhoneNumber())
                .build();
    }

    private List<OrderResponse> mapToOrderResponseList(List<Order> orders) {
        return orders.stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }




}