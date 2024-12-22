package com.oralie.orders.service;

import com.google.zxing.WriterException;
import com.oralie.orders.dto.request.OrderRequest;
import com.oralie.orders.dto.response.ListResponse;
import com.oralie.orders.dto.response.OrderItemResponse;
import com.oralie.orders.dto.response.OrderResponse;
import com.oralie.orders.exception.PaymentProcessingException;
import com.paypal.api.payments.Payment;
import org.springframework.core.io.InputStreamResource;

import java.awt.image.BufferedImage;
import java.io.IOException;

public interface OrderService {

    ListResponse<OrderResponse> getAllOrders(int page, int size, String sortBy, String sort);

    OrderResponse placeOrder(OrderRequest orderRequest) throws PaymentProcessingException;

    String checkoutOrder(String orderId);

    ListResponse<OrderResponse> getOrdersByUserId(String userId, int page, int size, String sortBy, String sort);

    ListResponse<OrderItemResponse> getOrderItemsByOrderId(Long orderId);

    OrderResponse viewOrder(Long idOrder);

    OrderResponse updateOrderStatus(Long orderId, String status);

    void updateOrderPaymentStatusByPayPalId(String paypalId, String status);

    String cancelOrder(Long orderId);

    InputStreamResource generateQRCodeImage(String qrcode) throws WriterException, IOException;

    InputStreamResource generateBarCodeImage(String barCode) throws WriterException, IOException;

    void deleteOrder(Long orderId);

    void updateRatedStatus(Long orderItemId);

    boolean checkOrderItemRated(Long orderItemId);
}
