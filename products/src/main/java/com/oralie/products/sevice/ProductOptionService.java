package com.oralie.products.sevice;

import com.oralie.products.dto.request.ProductOptionRequest;
import com.oralie.products.dto.response.ListResponse;
import com.oralie.products.dto.response.ProductOptionResponse;

import java.util.List;

public interface ProductOptionService {
    ListResponse<ProductOptionResponse> getAllProductOptions(int page, int size, String sortBy, String sort);

    ProductOptionResponse getProductOptionById(Long id);

    ProductOptionResponse createProductOption(ProductOptionRequest productOptionRequest);

    ProductOptionResponse updateProductOption(Long id, ProductOptionRequest productOptionRequest);

    void deleteProductOption(Long id);
}
