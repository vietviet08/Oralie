package com.oralie.orders.service.impl;

import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.EAN13Writer;
import com.google.zxing.qrcode.QRCodeWriter;
import com.oralie.orders.constant.OrderStatus;
import com.oralie.orders.constant.PaymentMethod;
import com.oralie.orders.constant.PaymentStatus;
import com.oralie.orders.dto.event.OrderItemEvent;
import com.oralie.orders.dto.event.OrderPlaceEvent;
import com.oralie.orders.dto.request.OrderRequest;
import com.oralie.orders.dto.response.ListResponse;
import com.oralie.orders.dto.response.OrderAddressResponse;
import com.oralie.orders.dto.response.OrderItemResponse;
import com.oralie.orders.dto.response.OrderResponse;
import com.oralie.orders.exception.PaymentProcessingException;
import com.oralie.orders.exception.ResourceNotFoundException;
import com.oralie.orders.model.Order;
import com.oralie.orders.model.OrderAddress;
import com.oralie.orders.model.OrderItem;
import com.oralie.orders.repository.OrderItemRepository;
import com.oralie.orders.repository.OrderRepository;
import com.oralie.orders.service.CartService;
import com.oralie.orders.service.OrderService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final OrderRepository orderRepository;

    private final OrderItemRepository orderItemRepository;

    private final CartService cartService;

    private final Gson gson;

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
    @Transactional
    public OrderResponse placeOrder(OrderRequest orderRequest) throws PaymentProcessingException {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        Order order = Order.builder()
                .userId(userId)
                .cartId(cartService.getCartIdByUserId())
                .address(OrderAddress.builder()
                        .addressDetail(orderRequest.getAddress().getAddressDetail())
                        .city(orderRequest.getAddress().getCity())
                        .email(orderRequest.getAddress().getEmail())
                        .phoneNumber(orderRequest.getAddress().getPhoneNumber())
                        .build())
                .orderItems(orderRequest.getOrderItems().stream()
                        .map(orderItemRequest -> OrderItem.builder()
                                .productId(orderItemRequest.getProductId())
                                .productName(orderItemRequest.getProductName())
                                .quantity(orderItemRequest.getQuantity())
                                .totalPrice(orderItemRequest.getTotalPrice())
                                .productImage(orderItemRequest.getProductImage())
                                .isRated(false)
                                .build())
                        .collect(Collectors.toList()))
                .totalPrice(orderRequest.getTotalPrice())
                .voucher(orderRequest.getVoucher())
                .discount(orderRequest.getDiscount())
                .shippingFee(orderRequest.getShippingFee())
                .status(OrderStatus.PENDING)
                .shippingMethod(orderRequest.getShippingMethod())
                .paymentMethod(orderRequest.getPaymentMethod())
                .paymentStatus(orderRequest.getPaymentStatus())
                .note(orderRequest.getNote())
                .build();

        order.getOrderItems().forEach(orderItem -> orderItem.setOrder(order));

        if (PaymentMethod.COD.name().equalsIgnoreCase(orderRequest.getPaymentMethod())) {
            order.setPaymentStatus(PaymentStatus.PENDING);
            order.setStatus(OrderStatus.PROCESSING);
            order.setPaymentMethod(PaymentMethod.COD.name());
            order.setPaymentStatus(PaymentStatus.PENDING);

        } else if (PaymentMethod.BANK_TRANSFER.name().equalsIgnoreCase(orderRequest.getPaymentMethod())) {
            order.setPaymentStatus(PaymentStatus.PENDING);
            order.setStatus(OrderStatus.PROCESSING);
            order.setPaymentMethod(PaymentMethod.BANK_TRANSFER.name());
            order.setPaymentStatus(PaymentStatus.COMPLETED);
        } else {
            throw new PaymentProcessingException("Invalid payment method");
        }

        orderRepository.save(order);

        OrderPlaceEvent orderPlacedEvent = OrderPlaceEvent.builder()
                .orderId(order.getId())
                .userId(order.getUserId())
                .email(order.getAddress().getEmail())
                .orderItems(order.getOrderItems().stream()
                        .map(orderItem -> OrderItemEvent.builder()
                                .productId(orderItem.getProductId())
                                .productName(orderItem.getProductName())
                                .quantity(orderItem.getQuantity())
                                .totalPrice(orderItem.getTotalPrice())
                                .build())
                        .collect(Collectors.toList()))
                .totalPrice(order.getTotalPrice())
                .discount(order.getDiscount())
                .shippingFee(order.getShippingFee())
                .status(order.getStatus())
                .shippingMethod(order.getShippingMethod())
                .paymentMethod(order.getPaymentMethod())
                .tokenViewOrder(order.getId() + UUID.randomUUID().toString())
                .build();

        log.info("Start- Sending OrderPlacedEvent {} to Kafka Topic", orderPlacedEvent);
        kafkaTemplate.send("order-placed-topic", gson.toJson(orderPlacedEvent));
        log.info("End- Sending OrderPlacedEvent {} to Kafka Topic", orderPlacedEvent);
        //subtract quantity product in inventory service

        kafkaTemplate.send("inventory-restock-topic", gson.toJson(orderPlacedEvent));

        //clear cart in cart service | need using kafka to send event to cart service & inventory also
        cartService.clearCart();

        return mapToOrderResponse(order);
    }


    @Override
    public String checkoutOrder(String orderId) {
        Order order = orderRepository.findById(Long.parseLong(orderId))
                .orElseThrow(() -> new ResourceNotFoundException("Order not found", "id", orderId));

        kafkaTemplate.send("checkout-order-topic", gson.toJson(PaymentOrderMessage.builder()
                .orderId(orderId)
                .userId(order.getUserId())
                .totalPrice(order.getTotalPrice())
                .currency("USD")
                .paymentMethod(order.getPaymentMethod())
                .build()));

        return OrderStatus.PROCESSING;
    }

    @KafkaListener(topics = "order-callback-topic", groupId = "order-callback-group")
    public void callBackOrder(String message) {

        log.info("Received message: {}", message);

        CallBackMessage callBackMessage = gson.fromJson(message, CallBackMessage.class);

        Order order = orderRepository.findById(Long.parseLong(callBackMessage.getOrderId()))
                .orElseThrow(() -> new ResourceNotFoundException("Order not found", "id", callBackMessage.getOrderId()));

        //TODO: update inventory service

        order.setPaymentStatus(callBackMessage.getPaymentStatus());

        orderRepository.save(order);

        //TODO: send message to notification service
        kafkaTemplate.send("notification-ordered-topic", gson.toJson(OrderPlaceEvent.builder()
                .orderId(order.getId())
                .userId(order.getUserId())
                .email(order.getAddress().getEmail())
                .totalPrice(order.getTotalPrice())
                .build()));
    }

    @Override
    public ListResponse<OrderResponse> getOrdersByUserId(String userId, int page, int size, String sortBy, String sort) {
        Sort sortObj = sort.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sortObj);
        Page<Order> pageOrders = orderRepository.findByUserId(userId, pageable);
        List<Order> orders = pageOrders.getContent();
        return ListResponse
                .<OrderResponse>builder()
                .data(mapToOrderResponseList(orders))
                .build();
    }

    @Override
    public ListResponse<OrderItemResponse> getOrderItemsByOrderId(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found", "id", orderId + ""));
        List<OrderItem> orderItems = order.getOrderItems();
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

        if (!isValidStatus(status)) {
            throw new IllegalArgumentException("Invalid order status: " + status);
        }

        order.setStatus(status);
        orderRepository.save(order);
        return mapToOrderResponse(order);
    }

    private boolean isValidStatus(String status) {
        return OrderStatus.PENDING.equals(status) || OrderStatus.PROCESSING.equals(status) ||
                OrderStatus.SHIPPING.equals(status) || OrderStatus.DELIVERED.equals(status) ||
                OrderStatus.CANCELLED.equals(status) || OrderStatus.RETURNED.equals(status) ||
                OrderStatus.REFUNDED.equals(status) || OrderStatus.FAILED.equals(status) ||
                OrderStatus.COMPLETED.equals(status) || OrderStatus.EXPIRED.equals(status);
    }

    @Override
    public void updateOrderPaymentStatusByPayPalId(String paypalId, String status) {
        Order order = orderRepository.findByPayId(paypalId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found", "paypalId", paypalId));
        order.setPaymentStatus(status);
        orderRepository.save(order);
    }

    @Override
    public String cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found", "id", orderId + ""));
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
        return "Order cancelled successfully";
    }

    @Override
    public InputStreamResource generateQRCodeImage(String qrcode) throws WriterException, IOException {

        log.info("Generating QR Code for: {}", qrcode);

        QRCodeWriter barcodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = barcodeWriter.encode(qrcode, BarcodeFormat.QR_CODE, 200, 200);

        BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(qrImage, "png", baos);
        ByteArrayInputStream imageStream = new ByteArrayInputStream(baos.toByteArray());

        return new InputStreamResource(imageStream);
    }

    @Override
    public InputStreamResource generateBarCodeImage(String barCode) throws WriterException, IOException {

        log.info("Generating Bar Code for: {}", barCode);

        EAN13Writer barcodeWriter = new EAN13Writer();
        BitMatrix bitMatrix = barcodeWriter.encode(barCode, BarcodeFormat.EAN_13, 300, 150);

        BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(qrImage, "png", baos);
        ByteArrayInputStream imageStream = new ByteArrayInputStream(baos.toByteArray());

        return new InputStreamResource(imageStream);
    }

    @Override
    public void deleteOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found", "id", orderId + ""));
        orderRepository.delete(order);
    }

    @Override
    public void updateRatedStatus(Long orderItemId) {
        OrderItem orderItem = orderItemRepository.findOrderItemById(orderItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Order item not found", "id", orderItemId + ""));

        orderItem.setRated(true);
        orderItemRepository.save(orderItem);
        log.info("Order item with id {} has been rated", orderItemId);
    }

    @Override
    public boolean checkOrderItemRated(Long orderItemId) {
        OrderItem orderItem = orderItemRepository.findOrderItemById(orderItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Order item not found", "id", orderItemId + ""));
        Order order = orderItem.getOrder();

        return orderItem.isRated() || !order.getStatus().equals(OrderStatus.DELIVERED);
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
                .linkPaypalToExecute(order.getLinkPaypalToExecute() != null ? order.getLinkPaypalToExecute() : "")
                .payId(order.getPayId() != null ? order.getPayId() : "")
                .build();
    }

    private List<OrderItemResponse> mapToOrderItemResponse(List<OrderItem> orderItems) {
        return orderItems.stream()
                .map(orderItem -> OrderItemResponse.builder()
                        .id(orderItem.getId())
                        .productId(orderItem.getProductId())
                        .productName(orderItem.getProductName())
                        .quantity(orderItem.getQuantity())
                        .totalPrice(orderItem.getTotalPrice())
                        .isRated(orderItem.isRated())
                        .build())
                .collect(Collectors.toList());
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


    @Getter
    @Builder
    @AllArgsConstructor
    private static class CallBackMessage {
        private String orderId;
        private String paymentStatus;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    private static class PaymentOrderMessage {
        private String orderId;
        private String userId;
        private Double totalPrice;
        private String currency;
        private String paymentMethod;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    private static class OrderMessage {
        private String orderId;
        private String status;
    }

}