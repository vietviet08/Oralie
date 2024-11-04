package com.oralie.notification.service;

import com.oralie.notification.dto.OrderPlaceEvent;
import org.springframework.kafka.annotation.KafkaListener;

public interface NotificationService {

    @KafkaListener(topics = "order-placed-topic", groupId = "order-placed-group")
    void orderPlaceListen(String message);

}
