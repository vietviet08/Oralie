package com.oralie.products.service;

import com.oralie.products.dto.request.ProductQuantityPost;
import com.oralie.products.dto.request.ProductRequest;
import com.oralie.products.dto.response.ListResponse;
import com.oralie.products.dto.response.ProductBaseResponse;
import com.oralie.products.dto.response.ProductResponse;
import com.oralie.products.dto.response.ProductResponseES;

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

    List<ProductBaseResponse> updateQuantityProduct(List<ProductQuantityPost> productQuantityPosts);

    void updateAliveProduct(Long id);

    void deleteProduct(Long id);

    boolean existingProductByProductId(Long productId);

    List<ProductResponse> top8ProductRelatedCategory(Long productId, String categoryName);

}
