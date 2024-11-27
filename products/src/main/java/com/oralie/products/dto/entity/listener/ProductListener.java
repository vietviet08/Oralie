package com.oralie.product.dto.entity.listenr;

@RequiredArgsConstructor
public class ProudctListener{
	
	private static final Logger log = LoggerFactory.getLogger(ProdudctListener.class);
	private final ProductRedisService productRedisService;

	@PrePersist
	public void prePersist(Product product){

	}

	@PostPersist
	public void postPersist(Product product){
		productRedisService.clear();
	}

	@PreUpdate
	public void preUpdate(Product product){
		
	}

	@PostUpdate
	public void postUpdate(Product product){
		productRedisService.clear();
	}

	@PreRemove
	public void preRemove(Product product){
		
	}

	@PostRemove
	public void postRemove(Product product){
		productRedisService.clear();
	}
}