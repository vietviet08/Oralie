package com.oralie.products.sevice.impl;

import com.oralie.products.model.Product;
import com.oralie.products.model.ProductSpecification;
import com.oralie.products.repository.ProductSpecificationRepository;
import com.oralie.products.sevice.ProductSpecificationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductSpecificationServiceImpl implements ProductSpecificationService {

    private static final Logger log = LoggerFactory.getLogger(ProductSpecificationServiceImpl.class);

    private final ProductSpecificationRepository productSpecificationRepository;

    @Override
    public Map<String, String> getProductSpecifications(Long productId) {
        List<ProductSpecification> productSpecification = productSpecificationRepository.findAllByProductId(productId);

        Map<String, String> productSpecifications = null;

        if (productSpecification != null && !productSpecification.isEmpty()) {

            log.info("Product specifications found for product id: {}", productId);

            productSpecifications = productSpecification.stream()
                    .collect(Collectors.toMap(ProductSpecification::getName, ProductSpecification::getValue));
        }

        return productSpecifications;

    }

    @Override
    public Map<String, String> saveProductSpecification(Long productId, Map<String, String> productSpecifications) {
        List<ProductSpecification> productSpecificationList = productSpecifications.entrySet().stream()
                .map(entry -> ProductSpecification.builder()
                        .name(entry.getKey())
                        .value(entry.getValue())
                        .product(Product.builder().id(productId).build())
                        .build())
                .toList();

        productSpecificationRepository.saveAll(productSpecificationList);
        return productSpecifications;
    }

    @Override
    public Map<String, String> updateProductSpecification(Long productId, Map<String, String> productSpecifications) {
        List<ProductSpecification> productSpecificationList = productSpecifications.entrySet().stream()
                .map(entry -> ProductSpecification.builder()
                        .name(entry.getKey())
                        .value(entry.getValue())
                        .product(Product.builder().id(productId).build())
                        .build())
                .toList();

        productSpecificationRepository.saveAll(productSpecificationList);
        return productSpecifications;
    }

    @Override
    public void deleteProductSpecification(Long productId) {
        List<ProductSpecification> productSpecifications = productSpecificationRepository.findAllByProductId(productId);
        productSpecificationRepository.deleteAll(productSpecifications);
    }

    @Override
    public void deleteProductSpecification(Long productId, String specificationName) {
        ProductSpecification productSpecification = productSpecificationRepository.findByProductIdAndName(productId, specificationName);
        productSpecificationRepository.delete(productSpecification);
    }
}
