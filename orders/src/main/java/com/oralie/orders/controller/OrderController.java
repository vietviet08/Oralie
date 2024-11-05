package com.oralie.orders.controller;

import com.oralie.orders.constant.PayPalConstant;
import com.oralie.orders.dto.OrderContactDto;
import com.oralie.orders.dto.request.OrderRequest;
import com.oralie.orders.dto.request.PayPalInfoRequest;
import com.oralie.orders.dto.response.ListResponse;
import com.oralie.orders.dto.response.OrderItemResponse;
import com.oralie.orders.dto.response.OrderResponse;
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
import org.springframework.web.servlet.view.RedirectView;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping(consumes = "application/json", produces = "application/json")
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

    @PutMapping("/dash/orders/{orderId}")
    public ResponseEntity<OrderResponse> updateOrderStatus(@PathVariable("orderId") Long orderId, @RequestParam String status) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(orderService.updateOrderStatus(orderId, status));
    }


    //store
    @PostMapping("/store/orders")
    public ResponseEntity<OrderResponse> createOrder(@RequestBody OrderRequest orderRequest) throws PaymentProcessingException {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(orderService.placeOrder(orderRequest));

        // if method is paypal will has the link success or cancel
        // if has the link success, we need get the link and redirect to that link
        // the link is contain name domain client not the api domain


        // flow is order -> payment(paylpal) -> success/cancel
        // order response contain link to payment (ex: localhost:3000/payment/paypal/scucess/.....????)
        // client will redirect to that link to execute payment
        // then redirect client will call api to execute payment (ex: "apigateway/api/orders/store/payment/success")

    }

    //response url to front & client use url to redirect page
    // this api will return the link to redirect to paypal if user click on button pay with paypal not click payment button
    // click payment button will call api create order @PostMapping("/store/orders")
    @PostMapping("/store/orders/paypal")
    public ResponseEntity<OrderResponse> createOrderWithPayPal(@RequestBody OrderRequest orderRequest) throws PaymentProcessingException {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(orderService.placeOrderWithoutPayPal(orderRequest));
    }

    //when client redirect to success page, call this api to execute payment
    // the link set in order response
    @GetMapping("/store/payment/success")
    public ResponseEntity<String> paymentSuccess(
            @RequestParam("paymentId") String paymentId,
            @RequestParam("PayerID") String payerId
    ) {
        try {
            Payment payment = payPalService.executePayment(paymentId, payerId);
            if (payment.getState().equals("approved")) {
                return ResponseEntity
                        .status(HttpStatus.OK)
                        .body(PayPalConstant.SUCCESS_MESSAGE);
            }
        } catch (PayPalRESTException e) {
            log.error("Error the payment please try again", e);
        }
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(PayPalConstant.ERROR_MESSAGE);
    }

    @GetMapping("/store/payment/cancel")
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

    @PutMapping("/store/orders/{orderId}/cancel")
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
