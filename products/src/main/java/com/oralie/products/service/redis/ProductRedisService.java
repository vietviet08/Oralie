package com.oralie.products.service.redis;

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