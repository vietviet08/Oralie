package com.oralie.products.service.redis.impl;


@Service
@RequiredArgsConstructor
public class ProudctRedisServiceImpl implements ProudctRedisService{
	
	private static final Logger log = LoggerFactory.getLogger(ProudctRedisServiceImpl.class);
	private final ProductRepository productRepository;
	private final RedisTemplate<String, Object> redisTemplate;
	private final ObjectMapper redisObjectMapper;

	@Override
	void clear(){
		redisTemplate.getConnectionFactory().getConnection().flushAll();
	}

	@Override
	ListResponse<ProductResponse> getAllProducts(int page,
												int size,
												String sortBy,
												String sort,
												String search){
		String key = generateKey(page, size, sortBy, sort, search);

		log.info("Key after generate in redis getAllProducts: {}", key);

		String json = (String)redisTemplate.opsForValue().get(key);

		log.info("Json after get from redis getAllProducts: {}", json);

		ListResponse<ProuductResponse> response = json != null ? redisObjectMapper.readValue(json, new TypeReference<ListResponse<ProudctReponse>>) : null;
		
		return response;
	}

	@Override
	void saveAllProduct(ListResponse<ProductResponse> listProductResponse,
						String sortBy,
						String sort, 
						String search){
		int page = listProductResponse.getPageNo();
		int size = listProductResponse.getPageSize();

		String key = generateKey(page, size, sortBy, sort, search);
		log.info("Key after generate in redis getAllProducts: {}", key);

		String json = redisObjectMapper.writeValueAsString(listProductResponse);
		log.info("Json after get from redis getAllProducts: {}", json);

		redisTemplate.opsForValue().set(key, json);
	}

	private String generateKey(int page, int size, String sortBy, String sort, String search){
		return String.format("products:%d:%d:%s:%s:%s", page, size, sortBy, sort, search);
	}
}