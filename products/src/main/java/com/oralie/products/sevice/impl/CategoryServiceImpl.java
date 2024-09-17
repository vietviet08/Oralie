package com.oralie.products.sevice.impl;

import com.oralie.products.dto.response.CategoryResponse;
import com.oralie.products.exception.ResourceNotFoundException;
import com.oralie.products.model.Brand;
import com.oralie.products.model.Category;
import com.oralie.products.repository.CategoryRepository;
import com.oralie.products.sevice.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public List<CategoryResponse> getAllCategories(int page, int size, String sortBy, String sort) {
        Sort sortObj = sort.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sortObj);

        List<Category> brands = categoryRepository.findAll(pageable).getContent();

        return mapToCategoryResponseList(brands);
    }

    @Override
    public CategoryResponse getCategoryById(Long id) {
        return mapToCategoryResponse(categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category not found", "id", id + "")));
    }

    @Override
    public CategoryResponse createCategory(CategoryResponse categoryResponse) {
        Category category = Category.builder()
                .name(categoryResponse.getName())
                .description(categoryResponse.getDescription())
                .image(categoryResponse.getImage())
                .isDeleted(categoryResponse.getIsDeleted())
                .parentCategory(Category.builder().id(categoryResponse.getParentId()).build())
                .build();
        categoryRepository.save(category);
        return mapToCategoryResponse(category);
    }

    @Override
    public CategoryResponse updateCategory(Long id, CategoryResponse categoryResponse) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category not found", "id", id + ""));
        category.setName(categoryResponse.getName());
        category.setDescription(categoryResponse.getDescription());
        category.setImage(categoryResponse.getImage());
        category.setIsDeleted(categoryResponse.getIsDeleted());
        category.setParentCategory(Category.builder().id(categoryResponse.getParentId()).build());
        categoryRepository.save(category);
        return mapToCategoryResponse(category);
    }

    @Override
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category not found", "id", id + ""));
        categoryRepository.delete(category);
    }

    private CategoryResponse mapToCategoryResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .image(category.getImage())
                .isDeleted(category.getIsDeleted())
                .parentId(category.getParentCategory().getId())
                .build();
    }

    private List<CategoryResponse> mapToCategoryResponseList(List<Category> categories) {
        return categories.stream().map(this::mapToCategoryResponse).collect(Collectors.toList());
    }

}
