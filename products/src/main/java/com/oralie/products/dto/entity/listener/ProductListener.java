package com.oralie.products.dto.entity.listener;

import com.oralie.products.model.Product;
import com.oralie.products.service.redis.ProductRedisService;
import jakarta.persistence.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequiredArgsConstructor
public class ProductListener {

    private static final Logger log = LoggerFactory.getLogger(ProductListener.class);
    private final ProductRedisService productRedisService;

    @PrePersist
    public void prePersist(Product product) {

    }

    @PostPersist
    public void postPersist(Product product) {
        productRedisService.clear();
    }

    @PreUpdate
    public void preUpdate(Product product) {

    }

    @PostUpdate
    public void postUpdate(Product product) {
        productRedisService.clear();
    }

    @PreRemove
    public void preRemove(Product product) {

    }

    @PostRemove
    public void postRemove(Product product) {
        productRedisService.clear();
    }
}