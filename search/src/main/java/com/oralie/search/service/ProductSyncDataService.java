package com.oralie.search.service;

import com.oralie.search.dto.response.ProductResponseES;

public interface ProductSyncDataService {

    ProductResponseES getProductResponseEsById(Long id);

    void createProduct(Long id);

    void updateProduct(Long id);

    void deleteProduct(Long id);
}
