package com.oralie.orders.controller;

import com.oralie.orders.constant.PayPalConstant;
import com.oralie.orders.dto.OrderContactDto;
import com.oralie.orders.dto.request.OrderRequest;
import com.oralie.orders.dto.response.ListResponse;
import com.oralie.orders.dto.response.OrderItemResponse;
import com.oralie.orders.dto.response.OrderResponse;
import com.oralie.orders.dto.response.paypal.PayerResponse;
import com.oralie.orders.dto.response.paypal.PaymentResponse;
import com.oralie.orders.dto.response.paypal.RedirectUrlsResponse;
import com.oralie.orders.dto.response.paypal.TransactionResponse;
import com.oralie.orders.exception.PaymentProcessingException;
import com.oralie.orders.service.OrderService;
import com.oralie.orders.service.PayPalService;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
public class OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    private final Environment environment;

    @Value("${info.app.version}")
    private String build;

    private final OrderContactDto orderContactDto;

    private final OrderService orderService;

    private final PayPalService payPalService;

    //dash
    @GetMapping("/dash/orders")
    public ResponseEntity<ListResponse<OrderResponse>> getOrdersInDash(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sort
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(orderService.getAllOrders(page, size, sortBy, sort));
    }

    @PostMapping("/dash/orders/{orderId}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(@PathVariable("orderId") Long orderId,
                                                           @RequestParam String status) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(orderService.updateOrderStatus(orderId, status));
    }

    @DeleteMapping("/dash/orders/{orderId}")
    public ResponseEntity<Void> deleteOrder(@PathVariable("orderId") Long orderId) {
        orderService.deleteOrder(orderId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    //store
//    @PostMapping("/store/orders")
//    public ResponseEntity<OrderResponse> createOrder(@RequestBody OrderRequest orderRequest) throws PaymentProcessingException {
//        return ResponseEntity
//                .status(HttpStatus.CREATED)
//                .body(orderService.placeOrder(orderRequest));

        // if method is paypal will has the link success or cancel
        // if has the link success, we need get the link and redirect to that link
        // the link is contain name domain client not the api domain


        // flow is order -> payment(paylpal) -> success/cancel
        // order response contain link to payment (ex: localhost:3000/payment/paypal/scucess/.....????)
        // client will redirect to that link to execute payment
        // then redirect client will call api to execute payment (ex: "apigateway/api/orders/store/payment/success")

//    }

    //response url to front & client use url to redirect page
    // this api will return the link to redirect to paypal if user click on button pay with paypal not click payment button
    // click payment button will call api create order @PostMapping("/store/orders")

    @PostMapping("/store/orders")
    public ResponseEntity<OrderResponse> createOrder(@RequestBody OrderRequest orderRequest) {
        try {
            return new ResponseEntity<>(orderService.placeOrder(orderRequest), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/store/orders/paypal")
    public ResponseEntity<OrderResponse> createOrderWithPayPal(
            @RequestBody OrderRequest orderRequest) {
        try {
            return new ResponseEntity<>(payPalService.placeOrderWithoutPayPal(orderRequest), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    //when client redirect to success page, call this api to execute payment
    // the link set in order response
    @GetMapping(value = "/store/orders/checkout/success")
    public ResponseEntity<PaymentResponse> paymentSuccess(
            @RequestParam("paymentId") String paymentId,
            @RequestParam("PayerID") String payerId
    ) {
        try {
            Payment payment = payPalService.executePayment(paymentId, payerId);

            PaymentResponse.PaymentResponseBuilder builder = PaymentResponse.builder()
                    .id(payment.getId())
                    .intent(payment.getIntent())
                    .payer(PayerResponse.builder()
                            .paymentMethod(payment.getPayer().getPaymentMethod())
                            .build())

                    .transactions(payment.getTransactions()
                            .stream().map(transaction -> TransactionResponse.builder()
                                    .amount(transaction.getAmount())
                                    .description(transaction.getDescription())
                                    .build())
                            .toList());

            if (payment.getRedirectUrls() != null) {
                builder.redirectUrls(RedirectUrlsResponse.builder()
                        .cancelUrl(payment.getRedirectUrls().getCancelUrl())
                        .returnUrl(payment.getRedirectUrls().getReturnUrl())
                        .build());
            }

            return new ResponseEntity<>(builder.build(), HttpStatus.OK);

        } catch (PayPalRESTException e) {
            log.error("Error the payment please try again", e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/store/orders/checkout/cancel")
    public ResponseEntity<String> paymentCancel() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(PayPalConstant.CANCEL_MESSAGE);
    }

    @GetMapping("/store/orders")
    public ResponseEntity<ListResponse<OrderResponse>> getOrders(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sort
    ) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(orderService.getOrdersByUserId(userId, page, size, sortBy, sort));
    }

    @GetMapping("/store/orders/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable("orderId") Long orderId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(orderService.viewOrder(orderId));
    }

    @GetMapping("/store/orders/{orderId}/items")
    public ResponseEntity<ListResponse<OrderItemResponse>> getOrderItemsByOrderId(@PathVariable("orderId") Long orderId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(orderService.getOrderItemsByOrderId(orderId));
    }

    @PostMapping("/store/orders/{orderId}/cancel")
    public ResponseEntity<String> cancelOrder(@PathVariable("orderId") Long orderId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(orderService.cancelOrder(orderId));
    }

    @PostMapping(path = "/store/orders/qrcode")
    public ResponseEntity<InputStreamResource> generateQRCodeImage(@RequestParam String qrCode) throws Exception {

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + qrCode + "\"")
                .body(orderService.generateQRCodeImage(qrCode));

    }

    @PostMapping(path = "/store/orders/bar-code", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<InputStreamResource> generateBarcode(@RequestParam String barCode) throws Exception {
        log.info("generateBarcode request: {}", barCode);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + barCode + "\"")
                .body(orderService.generateBarCodeImage(barCode));

    }

    @PutMapping("/store/orders/rated/{orderItemId}")
    public ResponseEntity<Void> updateRatedStatus(@PathVariable("orderItemId") Long orderItemId) {
        orderService.updateRatedStatus(orderItemId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @GetMapping("/store/orders/rated/{orderItemId}")
    public ResponseEntity<String> checkOrderItemRated(@PathVariable("orderItemId") Long orderItemId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(Boolean.toString(orderService.checkOrderItemRated(orderItemId)));
    }

    //info
    @GetMapping("/orders/build-version")
    public ResponseEntity<String> getBuildVersion() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(build);
    }

    @GetMapping("/orders/java-version")
    public ResponseEntity<String> getJavaVersion() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(environment.getProperty("JAVA_HOME"));
    }

    @GetMapping("/orders/contact-info")
    public ResponseEntity<OrderContactDto> getProductsContactDto() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(orderContactDto);
    }

}
