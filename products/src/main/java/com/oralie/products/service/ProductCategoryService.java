package com.oralie.products.service;

import com.oralie.products.dto.request.ProductCategoryRequest;
import com.oralie.products.dto.response.ProductCategoryResponse;

import java.util.List;

public interface ProductCategoryService {
    List<ProductCategoryResponse> getAllProductCategories(int page, int size, String sortBy, String sort);
    ProductCategoryResponse getProductCategoryById(Long id);
    ProductCategoryResponse createProductCategory(ProductCategoryRequest productCategoryRequest);
    ProductCategoryResponse updateProductCategory(Long id, ProductCategoryRequest productCategoryRequest);
    void deleteProductCategory(Long id);
}
