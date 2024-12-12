package com.oralie.products.service;

import com.oralie.products.dto.request.CategoryRequest;
import com.oralie.products.dto.response.CategoryResponse;
import com.oralie.products.dto.response.ListResponse;
import com.oralie.products.model.s3.FileMetadata;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CategoryService {

    ListResponse<CategoryResponse> getAllCategories(int page, int size, String sortBy, String sort, String search);

    List<CategoryResponse> getAllCategoriesNotId(Long id, boolean notId);

    List<CategoryResponse> getAllCategoryContainsName(String name);

    CategoryResponse getCategoryById(Long id);

    CategoryResponse createCategory(CategoryRequest categoryRequest);

    CategoryResponse updateCategory(Long id, CategoryRequest categoryRequest);

    void deleteCategory(Long id);

    FileMetadata uploadImage(MultipartFile file, Long id);

    void updateAvailable(Long id);

    void deleteImage(Long id);
}
