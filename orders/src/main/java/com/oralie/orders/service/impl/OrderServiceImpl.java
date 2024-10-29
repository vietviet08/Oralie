package com.oralie.orders.service.impl;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.EAN13Writer;
import com.google.zxing.qrcode.QRCodeWriter;
import com.oralie.orders.constant.OrderStatus;
import com.oralie.orders.constant.PaymentStatus;
import com.oralie.orders.dto.entity.OrderPlaceEvent;
import com.oralie.orders.dto.request.OrderRequest;
import com.oralie.orders.dto.request.PayPalInfoRequest;
import com.oralie.orders.dto.response.ListResponse;
import com.oralie.orders.dto.response.OrderAddressResponse;
import com.oralie.orders.dto.response.OrderItemResponse;
import com.oralie.orders.dto.response.OrderResponse;
import com.oralie.orders.exception.PaymentProcessingException;
import com.oralie.orders.exception.ResourceNotFoundException;
import com.oralie.orders.model.Order;
import com.oralie.orders.model.OrderAddress;
import com.oralie.orders.model.OrderItem;
import com.oralie.orders.repository.OrderRepository;
import com.oralie.orders.repository.client.CartFeignClient;
import com.oralie.orders.service.OrderService;
import com.oralie.orders.service.PayPalService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);
    private final KafkaTemplate<String, OrderPlaceEvent> kafkaTemplate;
    private final OrderRepository orderRepository;
    private final PayPalService payPalService;
    private final CartFeignClient cartFeignClient;

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
    public OrderResponse placeOrder(OrderRequest orderRequest) throws PaymentProcessingException {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        Order order = Order.builder()
                .userId(userId)
                .cartId(1L)
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

        if ("PAYPAL".equalsIgnoreCase(orderRequest.getPaymentMethod())) {
            try {
                PayPalInfoRequest payPalInfoRequest = PayPalInfoRequest.builder()
                        .currency("USD")
                        .total(order.getTotalPrice())
                        .description("Order payment")
                        .method("paypal")
                        .intent("sale")
                        .cancelUrl("http://localhost:3000/cancel")
                        .successUrl("http://localhost:3000/success")
                        .build();

                payPalService.createPayment(payPalInfoRequest);

                order.setPaymentStatus(PaymentStatus.COMPLETED);
                //need create payment model to store payment entity
//                order.setPaymentId(paymentId);  // Store payment ID from PayPal
            } catch (Exception e) {
                throw new PaymentProcessingException("Payment failed: " + e.getMessage());
            }
        }

        orderRepository.save(order);

        OrderPlaceEvent orderPlacedEvent = OrderPlaceEvent.builder()
                .orderId(order.getId())
                .userId(order.getUserId())
                .email(order.getAddress().getEmail())
                .totalPrice(order.getTotalPrice())
                .build();

        log.info("Start- Sending OrderPlacedEvent {} to Kafka Topic", orderPlacedEvent);

        kafkaTemplate.send("order-placed", orderPlacedEvent);

        log.info("End- Sending OrderPlacedEvent {} to Kafka Topic", orderPlacedEvent);

        //subtract quantity product in inventory service


        //clear cart in cart service
        cartFeignClient.clearCart();

        return mapToOrderResponse(order);
    }

    @Override
    public ListResponse<OrderResponse> getOrdersByUserId(String userId, int page, int size, String sortBy, String sort) {
        Sort sortObj = sort.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sortObj);
        Page<Order> pageOrders = orderRepository.findByUserId(userId, pageable);
        List<Order> orders = pageOrders.getContent();
        //        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
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
        order.setStatus(status);
        orderRepository.save(order);
        return mapToOrderResponse(order);
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

    private List<OrderItemResponse> mapToOrderItemResponse(List<OrderItem> orderItems) {
        return orderItems.stream()
                .map(orderItem -> OrderItemResponse.builder()
                        .id(orderItem.getId())
                        .productId(orderItem.getProductId())
                        .productName(orderItem.getProductName())
                        .quantity(orderItem.getQuantity())
                        .totalPrice(orderItem.getTotalPrice())
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


}