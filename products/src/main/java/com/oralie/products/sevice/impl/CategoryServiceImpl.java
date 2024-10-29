package com.oralie.products.sevice.impl;

import com.oralie.products.dto.request.CategoryRequest;
import com.oralie.products.dto.response.CategoryResponse;
import com.oralie.products.dto.response.ListResponse;
import com.oralie.products.exception.ResourceAlreadyExistException;
import com.oralie.products.exception.ResourceNotFoundException;
import com.oralie.products.model.Brand;
import com.oralie.products.model.Category;
import com.oralie.products.model.s3.FileMetadata;
import com.oralie.products.repository.CategoryRepository;
import com.oralie.products.repository.client.S3FeignClient;
import com.oralie.products.sevice.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final S3FeignClient s3FeignClient;

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
    @Transactional
    public CategoryResponse createCategory(CategoryRequest categoryRequest) {
        if (categoryRepository.existsByName(categoryRequest.getName())) {
            throw new ResourceAlreadyExistException("Category already exists with name " + categoryRequest.getName());
        }
        Category parentCategory = null;
        if (categoryRequest.getParentId() != null && !categoryRepository.existsById(categoryRequest.getParentId())) {
            throw new ResourceNotFoundException("Parent category not found", "id", categoryRequest.getParentId() + "");
        } else if (categoryRequest.getParentId() != null) {
            parentCategory = categoryRepository.findById(categoryRequest.getParentId()).orElseThrow(() -> new ResourceNotFoundException("Parent category not found", "id", categoryRequest.getParentId() + ""));
        }

        String slug = categoryRequest.getSlug();
        if (categoryRequest.getSlug() == null || categoryRequest.getSlug().isEmpty()) {
            slug = categoryRequest.getName().toLowerCase().replace(" ", "-");
        }

        Category category = Category.builder()
                .name(categoryRequest.getName())
                .slug(slug)
                .description(categoryRequest.getDescription())
                .image(categoryRequest.getImage())
                .isDeleted(categoryRequest.getIsDeleted())
                .parentCategory(categoryRequest.getParentId() != null ? parentCategory : null)
                .build();
        categoryRepository.save(category);

        return mapToCategoryResponse(category);
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryRequest categoryRequest) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category not found", "id", id + ""));
        Category parentCategory = null;
        if (categoryRequest.getParentId() != null) {
            if (id.equals(categoryRequest.getParentId())) {
                throw new RuntimeException("Category can't be parent of itself");
            }
            parentCategory = categoryRepository.findById(categoryRequest.getParentId()).orElseThrow(() -> new ResourceNotFoundException("Parent category not found", "id", categoryRequest.getParentId() + ""));
        }

        String slug = categoryRequest.getSlug();
        if (categoryRequest.getSlug() == null || categoryRequest.getSlug().isEmpty()) {
            slug = categoryRequest.getName().toLowerCase().replace(" ", "-");
        }

        category.setName(categoryRequest.getName());
        category.setSlug(slug);
        category.setDescription(categoryRequest.getDescription());
        category.setImage(categoryRequest.getImage());
        category.setIsDeleted(categoryRequest.getIsDeleted());
        category.setParentCategory(categoryRequest.getParentId() != null ? parentCategory : null);
        categoryRepository.save(category);
        return mapToCategoryResponse(category);
    }

    @Override
    public FileMetadata uploadImage(MultipartFile file, Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category not found", "id", id + ""));

        FileMetadata fileMetadata = Objects.requireNonNull(s3FeignClient.createAttachments(List.of(file)).getBody()).get(0);

        category.setImage(fileMetadata.getUrl());

        categoryRepository.save(category);

        return fileMetadata;
    }

    @Override
    public void deleteImage(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category not found", "id", id + ""));

        if (category.getImage() != null) {
            s3FeignClient.deleteFile(category.getImage());
        }

        category.setImage(null);

        categoryRepository.save(category);
    }

    @Override
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category not found", "id", id + ""));
        categoryRepository.delete(category);
    }

    private CategoryResponse mapToCategoryResponse(Category category) {
        Long parentCategoryId = (category.getParentCategory() != null) ? category.getParentCategory().getId() : null;
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .slug(category.getSlug())
                .description(category.getDescription())
                .image(category.getImage())
                .isDeleted(category.getIsDeleted())
                .parentId(parentCategoryId)
                .build();
    }

    private List<CategoryResponse> mapToCategoryResponseList(List<Category> categories) {
        return categories.stream().map(this::mapToCategoryResponse).collect(Collectors.toList());
    }

}


