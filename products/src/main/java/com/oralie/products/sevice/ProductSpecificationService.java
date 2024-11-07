package com.oralie.products.sevice;

import org.springframework.stereotype.Service;

import java.util.Map;

public interface ProductSpecificationService {

    Map<String, String> getProductSpecifications(Long productId);

    Map<String, String> saveProductSpecification(Long productId, Map<String, String> productSpecifications);

    Map<String, String> updateProductSpecification(Long productId, Map<String, String> productSpecifications);

    void deleteProductSpecification(Long productId);

    void deleteProductSpecification(Long productId, String specificationName);
}
