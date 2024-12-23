package com.oralie.orders.service.impl;

import com.oralie.orders.constant.OrderStatus;
import com.oralie.orders.constant.PayPalConstant;
import com.oralie.orders.constant.PaymentMethod;
import com.oralie.orders.constant.PaymentStatus;
import com.oralie.orders.dto.entity.OrderPlaceEvent;
import com.oralie.orders.dto.request.OrderRequest;
import com.oralie.orders.dto.request.PayPalInfoRequest;
import com.oralie.orders.dto.response.OrderAddressResponse;
import com.oralie.orders.dto.response.OrderItemResponse;
import com.oralie.orders.dto.response.OrderResponse;
import com.oralie.orders.exception.PaymentProcessingException;
import com.oralie.orders.model.Order;
import com.oralie.orders.model.OrderAddress;
import com.oralie.orders.model.OrderItem;
import com.oralie.orders.repository.OrderRepository;
import com.oralie.orders.service.CartService;
import com.oralie.orders.service.OrderService;
import com.oralie.orders.service.PayPalService;
import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.OAuthTokenCredential;
import com.paypal.base.rest.PayPalRESTException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PayPalServiceImpl implements PayPalService {

    private static final Logger log = LoggerFactory.getLogger(PayPalServiceImpl.class);

    @Value("${paypal.client-id}")
    private String clientId;

    @Value("${paypal.client-secret}")
    private String clientSecret;

    @Value("${paypal.mode}")
    private String mode;

    private final OrderService orderService;

    private final CartService cartService;

    private final OrderRepository orderRepository;

    public Payment placePaypalPayment(PayPalInfoRequest payPalInfoRequest) throws PayPalRESTException {

        APIContext apiContext = new APIContext(getAccessToken());
        apiContext.setConfigurationMap(getDefaultConfigurationMap());

        Payer payer = new Payer();
        payer.setPaymentMethod("paypal");

        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl(payPalInfoRequest.getCancelUrl());
        redirectUrls.setReturnUrl(payPalInfoRequest.getSuccessUrl());

        Details details = new Details();
        details.setShipping("0");
        details.setSubtotal(String.valueOf(payPalInfoRequest.getTotal()));
        details.setTax("0");

        Amount amount = new Amount();
        amount.setCurrency(payPalInfoRequest.getCurrency());
        amount.setTotal(String.valueOf(payPalInfoRequest.getTotal()));
        amount.setDetails(details);

        Transaction transaction = new Transaction();
        transaction.setDescription(payPalInfoRequest.getDescription());
        transaction.setAmount(amount);

        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        Payment payment = new Payment();
        payment.setIntent(payPalInfoRequest.getIntent());
        payment.setPayer(payer);
        payment.setRedirectUrls(redirectUrls);
        payment.setTransactions(transactions);

        return payment.create(apiContext);
    }


    public Payment executePayment(String paymentId, String payerId) throws PayPalRESTException {
        try {
            Payment payment = new Payment();
            payment.setId(paymentId);
            PaymentExecution paymentExecution = new PaymentExecution();
            paymentExecution.setPayerId(payerId);
            APIContext apiContext = new APIContext(getAccessToken());
            apiContext.setConfigurationMap(getDefaultConfigurationMap());

            Payment paymentExecuted = payment.execute(apiContext, paymentExecution);

            orderService.updateOrderPaymentStatusByPayPalId(paymentExecuted.getId(), paymentExecuted.getState());

            return paymentExecuted;
        } catch (PayPalRESTException e) {
            throw new PayPalRESTException("Error executing payment", e);
        }
    }

    @Override
    @Transactional
    public OrderResponse placeOrderWithoutPayPal(OrderRequest orderRequest) throws PaymentProcessingException {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        com.oralie.orders.model.Order order = Order.builder()
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
                                .productImage(orderItemRequest.getProductImage())
                                .quantity(orderItemRequest.getQuantity())
                                .totalPrice(orderItemRequest.getTotalPrice())
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

        Payment payment = null;
        String link = null;
        String payId = null;

        if (Objects.equals(order.getPaymentMethod(), PaymentMethod.PAYPAL.name())) {
            try {
                PayPalInfoRequest payPalInfoRequest = PayPalInfoRequest.builder()
                        .currency(PayPalConstant.PAYPAL_CURRENCY)
                        .total(order.getTotalPrice())
                        .description("Order payment")
                        .method("paypal")
                        .intent(PayPalConstant.PAYPAL_INTENT)
                        .cancelUrl(PayPalConstant.PAYPAL_CANCEL_URL)
                        .successUrl(PayPalConstant.PAYPAL_SUCCESS_URL)
                        .build();

                payment = this.placePaypalPayment(payPalInfoRequest);

                link = payment.getLinks().stream()
                        .filter(links -> links.getRel().equals("approval_url"))
                        .findFirst()
                        .orElseThrow(() -> new PaymentProcessingException("Approval URL not found"))
                        .getHref();

                payId = payment.getId();

                order.setLinkPaypalToExecute(link);
                order.setPayId(payId);
                order.setPaymentStatus(PaymentStatus.PENDING);
                order.setStatus(OrderStatus.PROCESSING);
                order.setPaymentMethod(PaymentMethod.PAYPAL.name());

            } catch (Exception e) {
                log.error("Payment failed for orderId: {} error: ", order.getId(), e);
                order.setPaymentStatus(PaymentStatus.FAILED);
                order.setStatus(OrderStatus.FAILED);
                orderRepository.save(order);
                throw new PaymentProcessingException("Payment failed: " + e.getMessage());
            }
        }

        OrderPlaceEvent orderPlacedEvent = OrderPlaceEvent.builder()
                .orderId(order.getId())
                .userId(order.getUserId())
                .email(order.getAddress().getEmail())
                .totalPrice(order.getTotalPrice())
                .build();

        log.info("Start- Sending OrderPlacedEvent {} to Kafka Topic", orderPlacedEvent);

//        kafkaTemplate.send("order-placed-topic", gson.toJson(orderPlacedEvent));

        log.info("End- Sending OrderPlacedEvent {} to Kafka Topic", orderPlacedEvent);

        //subtract quantity product in inventory service

        cartService.clearCart();

        return mapToOrderResponse(order);
    }

    private String getAccessToken() throws PayPalRESTException {
        return new OAuthTokenCredential(clientId, clientSecret, getDefaultConfigurationMap()).getAccessToken();
    }


    private java.util.Map<String, String> getDefaultConfigurationMap() {
        java.util.Map<String, String> configurationMap = new java.util.HashMap<>();
        configurationMap.put("mode", mode);
        return configurationMap;
    }

    private OrderResponse mapToOrderResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .cartId(order.getCartId())
                .address(OrderAddressResponse.builder()
                        .addressDetail(order.getAddress().getAddressDetail())
                        .city(order.getAddress().getCity())
                        .email(order.getAddress().getEmail())
                        .phoneNumber(order.getAddress().getPhoneNumber())
                        .build())
                .orderItems(order.getOrderItems().stream()
                        .map(orderItem -> OrderItemResponse.builder()
                                .productId(orderItem.getProductId())
                                .productName(orderItem.getProductName())
                                .productImage(orderItem.getProductImage())
                                .quantity(orderItem.getQuantity())
                                .totalPrice(orderItem.getTotalPrice())
                                .build())
                        .collect(Collectors.toList()))
                .totalPrice(order.getTotalPrice())
                .voucher(order.getVoucher())
                .discount(order.getDiscount())
                .shippingFee(order.getShippingFee())
                .status(order.getStatus())
                .shippingMethod(order.getShippingMethod())
                .paymentMethod(order.getPaymentMethod())
                .paymentStatus(order.getPaymentStatus())
                .note(order.getNote())
                .createdAt(order.getCreatedAt().toString())
                .build();
    }

}
