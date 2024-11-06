package com.oralie.search.service;

import com.oralie.search.dto.ProductParam;
import com.oralie.search.dto.response.ListResponse;
import com.oralie.search.model.ProductDocument;

import java.util.List;

public interface ProductSearchService {
    ListResponse<ProductDocument> searchProducts(ProductParam productParam);

}
