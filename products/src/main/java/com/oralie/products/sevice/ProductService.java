package com.oralie.products.sevice;

import com.oralie.products.dto.request.ProductRequest;
import com.oralie.products.dto.response.ListResponse;
import com.oralie.products.dto.response.ProductBaseResponse;
import com.oralie.products.dto.response.ProductResponse;
import com.oralie.products.dto.response.ProductResponseES;
import com.oralie.products.model.Product;

import java.util.List;

public interface ProductService {
    ListResponse<ProductResponse> getAllProducts(int page, int size, String sortBy, String sort, String search, String category);

    ListResponse<ProductResponse> getAllProductsByCategory( int page, int size, String sortBy, String sort, String categoryName);

    ListResponse<ProductResponse> getAllProductsByBrand( int page, int size, String sortBy, String sort,String categoryName, String brandName);

    ProductResponse getProductById(Long id);

    ProductResponseES getProductByIdES(Long id);

    ProductBaseResponse getProductBaseById(Long id);

    ProductResponse getProductBySlugs(String categoryName, String slug);

    ProductResponse getProductBySlug(String slug);

    ProductResponse createProduct(ProductRequest productRequest);

    ProductResponse updateProduct(Long id, ProductRequest productRequest);

    void deleteProduct(Long id);

}
