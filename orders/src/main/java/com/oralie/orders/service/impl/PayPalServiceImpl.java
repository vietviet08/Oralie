package com.oralie.orders.service.impl;

import com.oralie.orders.constant.OrderStatus;
import com.oralie.orders.constant.PayPalConstant;
import com.oralie.orders.constant.PaymentMethod;
import com.oralie.orders.constant.PaymentStatus;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PayPalServiceImpl implements PayPalService {

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
            log.info("Executing payment with ID: {} and payerID: {}", paymentId, payerId);
            
            // Check if payment has already been successfully processed
            try {
                boolean paymentAlreadyProcessed = orderService.checkIfOrderPaymentProcessed(paymentId);
                if (paymentAlreadyProcessed) {
                    log.info("Payment {} already processed, returning success response", paymentId);
                    // Return a simplified Payment object with the ID
                    Payment alreadyProcessedPayment = new Payment();
                    alreadyProcessedPayment.setId(paymentId);
                    alreadyProcessedPayment.setState("approved");
                    return alreadyProcessedPayment;
                }
            } catch (Exception e) {
                log.warn("Error checking if payment was already processed: {}", e.getMessage());
                // Continue with payment execution even if check fails
            }
            
            Payment payment = new Payment();
            payment.setId(paymentId);
            PaymentExecution paymentExecution = new PaymentExecution();
            paymentExecution.setPayerId(payerId);
            APIContext apiContext = new APIContext(getAccessToken());
            apiContext.setConfigurationMap(getDefaultConfigurationMap());

            Payment paymentExecuted = payment.execute(apiContext, paymentExecution);
            log.info("Payment executed successfully with state: {}", paymentExecuted.getState());

            // Update order payment status
            orderService.updateOrderPaymentStatusByPayPalId(paymentExecuted.getId(), paymentExecuted.getState());
            
            // Clear user's cart after successful payment
            try {
                cartService.clearCart();
                log.info("User's cart cleared after successful PayPal payment");
            } catch (Exception e) {
                log.error("Failed to clear cart after PayPal payment: {}", e.getMessage());
                // Don't fail the whole operation if cart clearing fails
            }

            return paymentExecuted;
        } catch (PayPalRESTException e) {
            log.error("PayPal error executing payment: {}", e.getMessage());
            
            // Check if it's the "PAYMENT_ALREADY_DONE" error
            if (e.getMessage() != null && e.getMessage().contains("PAYMENT_ALREADY_DONE")) {
                log.info("Payment already completed for ID: {}", paymentId);
                
                try {
                    // Mark the order as paid if it's not already
                    orderService.updateOrderPaymentStatusByPayPalId(paymentId, "approved");
                    
                    // Clear user's cart here too for PAYMENT_ALREADY_DONE case
                    try {
                        cartService.clearCart();
                        log.info("User's cart cleared after handling PAYMENT_ALREADY_DONE");
                    } catch (Exception cartException) {
                        log.error("Failed to clear cart for PAYMENT_ALREADY_DONE: {}", cartException.getMessage());
                    }
                    
                    // Return a simplified Payment object with the ID
                    Payment alreadyDonePayment = new Payment();
                    alreadyDonePayment.setId(paymentId);
                    alreadyDonePayment.setState("approved");
                    return alreadyDonePayment;
                } catch (Exception innerException) {
                    log.error("Error updating order after PAYMENT_ALREADY_DONE: {}", innerException.getMessage());
                }
            }
            
            throw new PayPalRESTException("Error executing payment", e);
        }
    }

    @Override
    @Transactional
    public OrderResponse placeOrderWithoutPayPal(OrderRequest orderRequest) throws PaymentProcessingException {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        
        log.info("Starting PayPal payment process for user: {}", userId);

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

                payment = placePaypalPayment(payPalInfoRequest);

                link = payment.getLinks().stream()
                        .filter(links -> links.getRel().equals("approval_url"))
                        .findFirst()
                        .orElseThrow(() -> new PaymentProcessingException("Approval URL not found"))
                        .getHref();

                log.info("Payment created with id: {}", payment.getId());
                log.info("link: {}", link);

                payId = payment.getId();

                order.setLinkPaypalToExecute(link);
                order.setPayId(payId);
                order.setPaymentStatus(PaymentStatus.PENDING);
                order.setStatus(OrderStatus.PROCESSING);
                order.setPaymentMethod(PaymentMethod.PAYPAL.name());
                
                // Save the order to ensure we have an ID
                Order savedOrder = orderRepository.save(order);
                
                // Note: We don't clear the cart here because the payment is not yet completed
                // Cart will be cleared after payment is executed
                
                OrderResponse response = mapToOrderResponse(savedOrder);
                
                log.info("Successfully created PayPal order: {}, with paypal link: {}", 
                         response.getId(), response.getLinkPaypalToExecute());
                
                return response;

            } catch (HttpClientErrorException.BadRequest e) {
                log.error("Bad request for orderId: {} error: {}", order.getId(), e.getMessage());
                order.setPaymentStatus(PaymentStatus.FAILED);
                order.setStatus(OrderStatus.FAILED);
                orderRepository.save(order);
                throw new PaymentProcessingException("Bad request: " + e.getMessage());
            } catch (Exception e) {
                log.error("Payment failed for orderId: {} error: {}", order.getId(), e.getMessage());
                order.setPaymentStatus(PaymentStatus.FAILED);
                order.setStatus(OrderStatus.FAILED);
                orderRepository.save(order);
                throw new PaymentProcessingException("Payment failed: " + e.getMessage());
            }
        }

        Order savedOrder = orderRepository.save(order);
        return mapToOrderResponse(savedOrder);
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
        log.debug("Mapping order to OrderResponse: {}", order.getId());
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
                                .isRated(orderItem.isRated())
                                .id(orderItem.getId())
                                .build())
                        .collect(Collectors.toList()))
                .totalPrice(order.getTotalPrice())
                .voucher(order.getVoucher())
                .discount(order.getDiscount())
                .shippingFee(order.getShippingFee())
                .shippingMethod(order.getShippingMethod())
                .paymentMethod(order.getPaymentMethod())
                .paymentStatus(order.getPaymentStatus())
                .status(order.getStatus())
                .payId(order.getPayId())
                .linkPaypalToExecute(order.getLinkPaypalToExecute())
                .note(order.getNote())
                .createdAt(order.getCreatedAt() != null ? order.getCreatedAt().toString() : null)
                .build();
    }

}
