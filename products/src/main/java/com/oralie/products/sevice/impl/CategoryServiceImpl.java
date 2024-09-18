package com.oralie.products.sevice.impl;

import com.oralie.products.dto.request.CategoryRequest;
import com.oralie.products.dto.response.CategoryResponse;
import com.oralie.products.dto.response.ListResponse;
import com.oralie.products.exception.ResourceAlreadyExistException;
import com.oralie.products.exception.ResourceNotFoundException;
import com.oralie.products.model.Brand;
import com.oralie.products.model.Category;
import com.oralie.products.repository.CategoryRepository;
import com.oralie.products.sevice.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
    public ListResponse<CategoryResponse> getAllCategories(int page, int size, String sortBy, String sort) {
        Sort sortObj = sort.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sortObj);
        Page<Category> pageCategory = categoryRepository.findAll(pageable);
        List<Category> categories = pageCategory.getContent();

        return ListResponse.<CategoryResponse>builder()
                .data(mapToCategoryResponseList(categories))
                .pageNo(pageCategory.getNumber())
                .pageSize(pageCategory.getSize())
                .totalElements((int) pageCategory.getTotalElements())
                .totalPages(pageCategory.getTotalPages())
                .isLast(pageCategory.isLast())
                .build();
    }

    @Override
    public CategoryResponse getCategoryById(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category not found", "id", id + ""));
        return mapToCategoryResponse(category);
    }

    @Override
    public CategoryResponse createCategory(CategoryRequest categoryRequest) {
        if (categoryRepository.existsByName(categoryRequest.getName())) {
            throw new ResourceAlreadyExistException("Category already exists with name " + categoryRequest.getName());
        }

        Category parentCategory = categoryRepository.findById(categoryRequest.getParentId()).orElseThrow(() -> new ResourceNotFoundException("Parent category not found", "id", categoryRequest.getParentId() + ""));

        Category category = Category.builder()
                .name(categoryRequest.getName())
                .description(categoryRequest.getDescription())
                .image(categoryRequest.getImage())
                .isDeleted(categoryRequest.getIsDeleted())
                .parentCategory(categoryRequest.getParentId() != null ? parentCategory : null)
                .build();
        categoryRepository.save(category);

        return mapToCategoryResponse(category);
    }

    @Override
    public CategoryResponse updateCategory(Long id, CategoryRequest categoryRequest) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category not found", "id", id + ""));
        if (categoryRepository.existsByName(categoryRequest.getName())) {
            throw new ResourceAlreadyExistException("Category already exists with name " + categoryRequest.getName());
        }
        Category parentCategory = null;
        if (categoryRequest.getParentId() != null) {
            if (id.equals(categoryRequest.getParentId())) {
                throw new RuntimeException("Category can't be parent of itself");
            }
            parentCategory = categoryRepository.findById(categoryRequest.getParentId()).orElseThrow(() -> new ResourceNotFoundException("Parent category not found", "id", categoryRequest.getParentId() + ""));
        }
        category.setName(categoryRequest.getName());
        category.setDescription(categoryRequest.getDescription());
        category.setImage(categoryRequest.getImage());
        category.setIsDeleted(categoryRequest.getIsDeleted());
        category.setParentCategory(categoryRequest.getParentId() != null ? parentCategory : null);
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


