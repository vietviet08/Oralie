package com.oralie.products.service.redis.impl;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oralie.products.dto.response.ListResponse;
import com.oralie.products.dto.response.ProductResponse;
import com.oralie.products.repository.ProductRepository;
import com.oralie.products.service.redis.ProductRedisService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ProductRedisServiceImpl implements ProductRedisService {

    private static final Logger log = LoggerFactory.getLogger(ProductRedisServiceImpl.class);
    private final ProductRepository productRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper redisObjectMapper;

    @Override
    public void clear() {
        Objects.requireNonNull(redisTemplate.getConnectionFactory()).getConnection().flushAll();
    }


    @Override
    public ListResponse<ProductResponse> getAllProduct(int page, int size, String sortBy, String sort, String search) throws

            JsonProcessingException {
        String key = generateKey(page, size, sortBy, sort, search);

        log.info("Key after generate in redis getAllProducts: {}", key);

        String json = (String) redisTemplate.opsForValue().get(key);

        log.info("Json after get from redis getAllProducts: {}", json);

        return json != null ? redisObjectMapper.readValue(json, new TypeReference<ListResponse<ProductResponse>>() {
        }) : null;
    }

    @Override
    public void saveAllProduct(ListResponse<ProductResponse> listProductResponse, String sortBy, String sort, String search) throws JsonProcessingException {
        int page = listProductResponse.getPageNo();
        int size = listProductResponse.getPageSize();

        String key = generateKey(page, size, sortBy, sort, search);
        log.info("Key after generate in redis getAllProducts: {}", key);

        String json = redisObjectMapper.writeValueAsString(listProductResponse);
        log.info("Json after get from redis getAllProducts: {}", json);

        redisTemplate.opsForValue().set(key, json);
    }

    private String generateKey(int page, int size, String sortBy, String sort, String search) {
        return String.format("products:%d:%d:%s:%s:%s", page, size, sortBy, sort, search);
    }
}