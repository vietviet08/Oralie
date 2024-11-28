package com.oralie.products.service.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.oralie.products.dto.response.ListResponse;
import com.oralie.products.dto.response.ProductResponse;

public interface ProductRedisService{

	void clear();

	ListResponse<ProductResponse> getAllProduct(int page,
												int size,
												String sortBy,
												String sort,
												String search) throws JsonProcessingException;

	void saveAllProduct(ListResponse<ProductResponse> listProductResponse,
						String sortBy,
						String sort, 
						String search) throws JsonProcessingException;
}