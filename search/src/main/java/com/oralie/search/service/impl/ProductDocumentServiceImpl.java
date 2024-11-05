package com.oralie.search.service.impl;

import com.oralie.search.dto.response.ProductResponseES;
import com.oralie.search.model.ProductDocument;
import com.oralie.search.repository.ProductDocumentRepository;
import com.oralie.search.service.ProductDocumentService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
@RequiredArgsConstructor
public class ProductDocumentServiceImpl implements ProductDocumentService {

    private static final Logger log = LoggerFactory.getLogger(ProductDocumentServiceImpl.class);
    private static final String URL_PRODUCT = "http://localhost:8081";

    private final RestClient restClient;

    private final ProductDocumentRepository productDocumentRepository;

    @Retry(name = "productRetry")
    @CircuitBreaker(name = "productCircuitBreaker", fallbackMethod = "getProductResponseESFallback")
    private ProductResponseES getProductById(Long productId) {

        log.info("Fetching product with id: {}", productId);

        final String jwtToken = SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();

        final URI url = UriComponentsBuilder
                .fromHttpUrl(URL_PRODUCT)
                .path("/store/products/products-es/{id}")
                .buildAndExpand(productId)
                .toUri();

        return restClient.get()
                .uri(url)
                .header("Authorization", "Bearer " + jwtToken)
                .retrieve()
                .body(ProductResponseES.class);
    }

    @Override
    public void saveProductDocument(Long productId) {
        log.info("Saving product with id: {}", productId);

        ProductResponseES productResponseES = getProductById(productId);

        log.info("Product fetched: {}", productResponseES);

        productDocumentRepository.save(mapToProductDocument(productResponseES));
    }

    @Override
    public void updateProductDocument(Long productId) {
        log.info("Updating product with id: {}", productId);

        ProductResponseES productResponseES = getProductById(productId);

        log.info("Product fetched: {}", productResponseES);

        productDocumentRepository.save(mapToProductDocument(productResponseES));
    }

    @Override
    public void deleteProductDocument(Long productId) {
        log.info("Deleting product with id: {}", productId);

        productDocumentRepository.deleteById(productId);

        log.info("Product deleted: {}", productId);
    }

    private ProductDocument mapToProductDocument(ProductResponseES productResponseES) {
        return ProductDocument.builder()
                .id(productResponseES.getId())
                .productName(productResponseES.getProductName())
                .slug(productResponseES.getSlug())
                .categories(productResponseES.getCategories())
                .options(productResponseES.getOptions())
                .brand(productResponseES.getBrand())
                .sku(productResponseES.getSku())
                .description(productResponseES.getDescription())
                .price(productResponseES.getPrice())
                .isFeatured(productResponseES.getIsFeatured())
                .isPromoted(productResponseES.getIsPromoted())
                .isAvailable(productResponseES.getIsAvailable())
                .isDeleted(productResponseES.getIsDeleted())
                .isDiscounted(productResponseES.getIsDiscounted())
                .quantity(productResponseES.getQuantity())
                .discount(productResponseES.getDiscount())
                .build();
    }

    protected ProductResponseES getProductResponseESFallback(Throwable throwable) throws Throwable {
        handleError(throwable);
        return null;
    }

    private void handleError(Throwable throwable) throws Throwable {
        log.error("Circuit breaker records an error. Detail {}", throwable.getMessage());
        throw throwable;
    }
}
