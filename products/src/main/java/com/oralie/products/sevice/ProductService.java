package com.oralie.products.sevice;

import com.oralie.products.dto.request.ProductRequest;
import com.oralie.products.dto.response.ListResponse;
import com.oralie.products.dto.response.ProductResponse;

import java.util.List;

public interface ProductService {
    ListResponse<ProductResponse> getAllProducts(int page, int size, String sortBy, String sort);

    ProductResponse getProductById(Long id);

    ProductResponse createProduct(ProductRequest productRequest);

    ProductResponse updateProduct(Long id, ProductRequest productRequest);

    void deleteProduct(Long id);

}
