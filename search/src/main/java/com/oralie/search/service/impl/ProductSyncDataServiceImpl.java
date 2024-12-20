package com.oralie.search.service.impl;

import com.oralie.search.dto.response.ProductResponseES;
import com.oralie.search.exception.ResourceNotFoundException;
import com.oralie.search.model.ProductDocument;
import com.oralie.search.repository.ProductDocumentRepository;
import com.oralie.search.service.ProductSyncDataService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
@RequiredArgsConstructor
public class ProductSyncDataServiceImpl implements ProductSyncDataService {

    private static final Logger log = LoggerFactory.getLogger(ProductSyncDataServiceImpl.class);

    private final ProductDocumentRepository productDocumentRepository;
    private final RestClient restClient;

    @Value("${url.products}")
    private String urlProducts;

    @Override
    public ProductResponseES getProductResponseEsById(Long id) {
        final URI url = UriComponentsBuilder.fromHttpUrl(urlProducts)
                .path("/store/products/products-es/{id}")
                .buildAndExpand(id).toUri();
        return restClient.get()
                .uri(url)
                .retrieve()
                .body(ProductResponseES.class);
    }

    @Override
    public void createProduct(Long id) {
        ProductResponseES productResponseES = getProductResponseEsById(id);

        ProductDocument productDocument = ProductDocument.builder()
                .id(productResponseES.getId())
                .productName(productResponseES.getProductName())
                .sku(productResponseES.getSku())
                .slug(productResponseES.getSlug())
                .description(productResponseES.getDescription())
                .price(productResponseES.getPrice())
                .isDiscounted(productResponseES.getIsDiscounted())
                .discount(productResponseES.getDiscount())
                .brand(productResponseES.getBrand())
                .categories(productResponseES.getCategories())
                .options(productResponseES.getOptions())
                .isAvailable(productResponseES.getIsAvailable())
                .isDeleted(productResponseES.getIsDeleted())
                .isFeatured(productResponseES.getIsFeatured())
                .isPromoted(productResponseES.getIsPromoted())
                .build();

        productDocumentRepository.save(productDocument);

        log.info("Product with id {} has been created", id);
    }

    @Override
    public void updateProduct(Long id) {
        ProductResponseES productResponseES = getProductResponseEsById(id);
        ProductDocument productDocument = productDocumentRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Product", "id", id.toString())
        );

        if (productResponseES.getIsDeleted()) {
            productDocumentRepository.delete(productDocument);
        } else {
            productDocument.setProductName(productResponseES.getProductName());
            productDocument.setSku(productResponseES.getSku());
            productDocument.setSlug(productResponseES.getSlug());
            productDocument.setDescription(productResponseES.getDescription());
            productDocument.setPrice(productResponseES.getPrice());
            productDocument.setIsDiscounted(productResponseES.getIsDiscounted());
            productDocument.setDiscount(productResponseES.getDiscount());
            productDocument.setBrand(productResponseES.getBrand());
            productDocument.setCategories(productResponseES.getCategories());
            productDocument.setOptions(productResponseES.getOptions());
            productDocument.setIsAvailable(productResponseES.getIsAvailable());
            productDocument.setIsDeleted(productResponseES.getIsDeleted());
            productDocument.setIsFeatured(productResponseES.getIsFeatured());
            productDocument.setIsPromoted(productResponseES.getIsPromoted());

            productDocumentRepository.save(productDocument);

            log.info("Product with id {} has been updated", id);
        }
    }

    @Override
    public void deleteProduct(Long id) {
        ProductDocument productDocument = productDocumentRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Product", "id", id.toString())
        );
        productDocumentRepository.delete(productDocument);

        log.info("Product with id {} has been deleted", id);
    }
}
