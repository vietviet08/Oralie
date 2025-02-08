package com.oralie.notification.service.impl;

import com.google.gson.Gson;
import com.oralie.notification.dto.AccountResponse;
import com.oralie.notification.dto.OrderItemResponse;
import com.oralie.notification.dto.event.OrderItemEvent;
import com.oralie.notification.dto.event.OrderPlaceEvent;
import com.oralie.notification.dto.OrderResponse;
import com.oralie.notification.repository.client.account.AccountFeignClient;
import com.oralie.notification.repository.client.order.OrderFeignClient;
import com.oralie.notification.service.NotificationService;
import com.oralie.notification.service.ThymeleafService;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final JavaMailSender javaMailSender;
    private final ThymeleafService thymeleafService;
    private final AccountFeignClient accountFeignClient;
    private final OrderFeignClient orderFeignClient;

    private final Gson gson;

    @Value("${spring.mail.username}")
    private String email;

    @KafkaListener(topics = "order-placed-topic", groupId = "order-group")
    @Override
    public void orderPlaceListen(String message) {
        OrderPlaceEvent orderPlacedEvent = gson.fromJson(message, OrderPlaceEvent.class);

        log.info("Got Message from order-placed topic {}", orderPlacedEvent);

//        AccountResponse customer = accountFeignClient.getAccountProfile().getBody();
//        OrderResponse order = orderFeignClient.getOrderById(orderPlacedEvent.getOrderId()).getBody();
        try {
            MimeMessage messageMail = javaMailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(
                    messageMail,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name()
            );

            helper.setFrom(new InternetAddress(email, "Oralie"));
            helper.setTo(orderPlacedEvent.getEmail());

            Map<String, Object> variable = new HashMap<>();
            variable.put("fullName", orderPlacedEvent.getFirstName() + " " + orderPlacedEvent.getLastName());
            variable.put("email", orderPlacedEvent.getEmail());
            variable.put("codeViewOrder", orderPlacedEvent.getTokenViewOrder());

            List<OrderItemEvent> orderDetailList = orderPlacedEvent.getOrderItems();
            variable.put("idOrder", orderPlacedEvent.getOrderId());
            variable.put("products", orderDetailList);

            String subPrice = formatCurrency(orderPlacedEvent.getTotalPrice() + orderPlacedEvent.getDiscount() + orderPlacedEvent.getShippingFee());
            variable.put("subPrice", subPrice);

            String totalPrice = formatCurrency(orderPlacedEvent.getTotalPrice());
            variable.put("totalPrice", totalPrice);

            String salePrice = formatCurrency(orderPlacedEvent.getDiscount());
            variable.put("salePrice", salePrice);

            String shippingFee = formatCurrency(orderPlacedEvent.getShippingFee());
            variable.put("shippingFee", shippingFee);

            variable.put("paymentMethod", orderPlacedEvent.getPaymentMethod());
//            variable.put("deliveryAddress", orderPlacedEvent.getAd);
            helper.setText(thymeleafService.createContent("mail-orders", variable), true);
            helper.setSubject("Your order in Oralie has been placed successfully");
            javaMailSender.send(messageMail);
        } catch (Exception e) {
            log.error("Error when sending email: ", e);
        }
    }

    @Override
    @KafkaListener(topics = "inventory-topic", groupId = "inventory-group")
    public void receiveTestInventory(String message) {
        log.info("Got Message from inventory-topic {}", message);

        try {
            MimeMessage messageMail = javaMailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(
                    messageMail,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name()
            );

            helper.setFrom(new InternetAddress(email, "Oralie"));
            helper.setTo("lambolambo2805@gmail.com");

            Map<String, Object> variable = new HashMap<>();
            variable.put("fullName", "Lambo San");
            variable.put("username", "lambosan");
            variable.put("email", "lambolambo2805@gmail.com");

            variable.put("phone", "0123456789");

            List<OrderItemResponse> orderDetailList = List.of(
                    new OrderItemResponse(1L, 100L, "product1", "", 1, 400.0, false),
                    new OrderItemResponse(2L, 120L, "product2", "", 2, 120.0, false),
                    new OrderItemResponse(3L, 130L, "product3", "", 3, 840.0, false)
            );

            variable.put("idOrder", "123");
            variable.put("products", orderDetailList);

            String subPrice = formatCurrency(1360.0);
            variable.put("subPrice", subPrice);

            String totalPrice = formatCurrency(1360.0);
            variable.put("totalPrice", totalPrice);

            String salePrice = formatCurrency(0.0);
            variable.put("salePrice", salePrice);

            String shippingFee = formatCurrency(0.0);
            variable.put("shippingFee", shippingFee);

            variable.put("paymentMethod", "COD");
            variable.put("deliveryAddress", "Hanoi");
            variable.put("codeViewOrder", "123-456-789");

            helper.setText(thymeleafService.createContent("mail-orders", variable), true);
            helper.setSubject("Your order in Oralie has been placed successfully");
            javaMailSender.send(messageMail);
        } catch (Exception e) {
            log.error("Error when sending email: ", e);
        }
    }

    public String formatCurrency(double amount) {
        Locale vietnam = new Locale("eng", "US");
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(vietnam);
        return currencyFormatter.format(amount);
    }

    private byte[] readAllBytes(InputStream inputStream) throws IOException {
        try {
            if (inputStream == null) {
                throw new RuntimeException("Resource not found");
            }
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }
            inputStream.close();
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}