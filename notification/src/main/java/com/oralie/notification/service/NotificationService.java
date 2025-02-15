package com.oralie.notification.service;

import org.springframework.kafka.annotation.KafkaListener;

public interface NotificationService {

    @KafkaListener(topics = "order-placed-topic", groupId = "notification-group")
    void orderPlaceListen(String message);

    @KafkaListener(topics = "inventory-topic", groupId = "inventory-group")
    void receiveTestInventory(String message);
}
