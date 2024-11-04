package com.oralie.notification.service.impl;

import com.google.gson.Gson;
import com.oralie.notification.dto.AccountResponse;
import com.oralie.notification.dto.OrderItemResponse;
import com.oralie.notification.dto.OrderPlaceEvent;
import com.oralie.notification.dto.OrderResponse;
import com.oralie.notification.repository.client.account.AccountFeignClient;
import com.oralie.notification.repository.client.order.OrderFeignClient;
import com.oralie.notification.service.NotificationService;
import com.oralie.notification.service.ThymeleafService;
import jakarta.mail.internet.MimeMessage;
import lombok.NoArgsConstructor;
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

    @KafkaListener(topics = "order-placed-topic", groupId = "order-placed-group")
    @Override
    public void orderPlaceListen(String message) {

        OrderPlaceEvent orderPlacedEvent = gson.fromJson(message, OrderPlaceEvent.class);

        log.info("Got Message from order-placed topic {}", orderPlacedEvent);

        AccountResponse customer = accountFeignClient.getAccountProfile().getBody();
        OrderResponse order = orderFeignClient.getOrderById(orderPlacedEvent.getOrderId()).getBody();

        try {

            MimeMessage messageMail = javaMailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(
                    messageMail,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name()
            );

            helper.setFrom(email);
            helper.setTo(customer.getEmail());

            Map<String, Object> variable = new HashMap<>();
            variable.put("fullName", customer.getFullName());
            variable.put("username", customer.getUsername());
            variable.put("email", customer.getEmail());
//            variable.put("codeViewOrder", order.getCodeViewOrder());

            if (customer.getAddress().get(0).getPhone() != null) variable.put("phone", customer.getAddress().get(0).getPhone());

            List<OrderItemResponse> orderDetailList = order.getOrderItems();
            variable.put("idOrder", order.getId());
            variable.put("products", orderDetailList);

            String subPrice = formatCurrency(order.getTotalPrice() + order.getDiscount() + order.getShippingFee());
            variable.put("subPrice", subPrice);

            String totalPrice = formatCurrency(order.getTotalPrice());
            variable.put("totalPrice", totalPrice);

            String salePrice = formatCurrency(order.getDiscount());
            variable.put("salePrice", salePrice);

            String shippingFee = formatCurrency(order.getShippingFee());
            variable.put("shippingFee", shippingFee);

            variable.put("paymentMethod", order.getPaymentMethod());
            variable.put("deliveryAddress", order.getAddress());
//            variable.put("codeViewOrder", order.getCodeViewOrder());

            helper.setText(thymeleafService.createContent("mail-orders", variable), true);
            helper.setSubject("Your order in Oralie has been placed successfully");
            javaMailSender.send(messageMail);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String formatCurrency(double amount) {
        Locale vietnam = new Locale("vi", "VN");
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