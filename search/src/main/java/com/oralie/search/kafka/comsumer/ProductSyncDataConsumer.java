package com.oralie.search.kafka.comsumer;

import com.oralie.search.dto.entity.Operation;
import com.oralie.search.dto.entity.ProductCdcMessage;
import com.oralie.search.dto.entity.ProductMsgKey;
import com.oralie.search.dto.entity.kafka.BaseCdcConsumer;
import com.oralie.search.dto.entity.kafka.RetrySupportDql;
import com.oralie.search.service.ProductSyncDataService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ProductSyncDataConsumer extends BaseCdcConsumer<ProductMsgKey, ProductCdcMessage> {

    private final ProductSyncDataService productSyncDataService;

    public ProductSyncDataConsumer(ProductSyncDataService productSyncDataService) {
        this.productSyncDataService = productSyncDataService;
    }

    @KafkaListener(
            id = "product-sync-es",
            groupId = "product-sync-search",
            topics = "${product.topic.name}"
//            containerFactory = "productCdcListenerContainerFactory"
    )
//    @RetrySupportDql(listenerContainerFactory = "productCdcListenerContainerFactory")
    public void processMessage(
            @Header(KafkaHeaders.RECEIVED_KEY) ProductMsgKey key,
            @Payload(required = false) @Valid ProductCdcMessage productCdcMessage,
            @Headers MessageHeaders headers
    ) {
        processMessage(key, productCdcMessage, headers, this::sync);
    }

    public void sync(ProductMsgKey key, ProductCdcMessage productCdcMessage) {
        boolean isHardDeleteEvent = productCdcMessage == null || Operation.DELETE.equals(productCdcMessage.getOp());
        if (isHardDeleteEvent) {
            log.warn("Having hard delete event for product: '{}'", key.getId());
            productSyncDataService.deleteProduct(key.getId());
        } else {
            var operation = productCdcMessage.getOp();
            var productId = key.getId();
            switch (operation) {
                case CREATE, READ -> productSyncDataService.createProduct(productId);
                case UPDATE -> productSyncDataService.updateProduct(productId);
                default -> log.warn("Unsupported operation '{}' for product: '{}'", operation, productId);
            }
        }
    }
}