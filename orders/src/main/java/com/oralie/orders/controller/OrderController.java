package com.oralie.orders.controller;

import com.oralie.orders.dto.OrderContactDto;
import com.oralie.orders.dto.request.OrderRequest;
import com.oralie.orders.dto.response.ListResponse;
import com.oralie.orders.dto.response.OrderItemResponse;
import com.oralie.orders.dto.response.OrderResponse;
import com.oralie.orders.exception.PaymentProcessingException;
import com.oralie.orders.service.OrderService;
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

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

@RestController
@RequiredArgsConstructor
//@RequestMapping(consumes = "application/json", produces = "application/json")
public class OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    private final Environment environment;

    @Value("${info.app.version}")
    private String build;

    private final OrderContactDto orderContactDto;

    private final OrderService orderService;


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
