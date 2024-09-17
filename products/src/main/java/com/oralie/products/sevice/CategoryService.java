package com.oralie.products.sevice;

import com.oralie.products.dto.response.CategoryResponse;

import java.util.List;

public interface CategoryService {
    List<CategoryResponse> getAllCategories(int page, int size, String sortBy, String sort);
    CategoryResponse getCategoryById(Long id);
    CategoryResponse createCategory(CategoryResponse categoryResponse);
    CategoryResponse updateCategory(Long id, CategoryResponse categoryResponse);
    void deleteCategory(Long id);
}
