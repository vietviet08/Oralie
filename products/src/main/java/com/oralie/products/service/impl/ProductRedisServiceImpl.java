package com.oralie.products.service.impl;


@Service
@RequiredArgsConstructor
public class ProudctRedisServiceImpl implements ProudctRedisService{
	
	private final ProductRepository productRepository;
	private final RedisTemplate<String, Object> redisTemplate;

	@Override
	void clear(){


	}

	@Override
	ListResponse<ProductResponse> getAllProducts(int page,
												int size,
												String sortBy,
												String sort,
												String search){

	}

	@Override
	void saveAllProduct(ListResponse<ProductResponse>,
						String sortBy,
						String sort, 
						String search){

	}
}