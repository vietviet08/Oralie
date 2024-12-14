package com.oralie.products.service.impl;

import com.oralie.products.dto.request.CategoryRequest;
import com.oralie.products.dto.response.CategoryResponse;
import com.oralie.products.dto.response.ListResponse;
import com.oralie.products.exception.ResourceAlreadyExistException;
import com.oralie.products.exception.ResourceNotFoundException;
import com.oralie.products.model.Category;
import com.oralie.products.model.s3.FileMetadata;
import com.oralie.products.repository.CategoryRepository;
import com.oralie.products.service.CategoryService;
import com.oralie.products.service.SocialService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    private final SocialService socialService;

    @Override
    public ListResponse<CategoryResponse> getAllCategories(int page, int size, String sortBy, String sort, String search) {
        Sort sortObj = sort.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sortObj);

        Page<Category> pageCategory;

        if (search != null && !search.isEmpty()) {
            pageCategory = categoryRepository.findByNameContainingIgnoreCase(search, pageable);
        } else {
            pageCategory = categoryRepository.findAll(pageable);
        }

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
    public CategoryResponse getCategoryByName(String name) {
        Category category = categoryRepository.findByName(name).orElseThrow(() -> new ResourceNotFoundException("Category not found", "name", name));
        return mapToCategoryResponse(category);
    }

    @Override
    public List<CategoryResponse> getAllCategoriesNotId(Long id, boolean notId) {
        if (notId) {
            return mapToCategoryResponseList(categoryRepository.findAllByIdNot(id));
        }
        return mapToCategoryResponseList(categoryRepository.findAll());
    }

    @Override
    public List<CategoryResponse> getAllCategoryContainsName(String name) {
        return mapToCategoryResponseList(categoryRepository.findByNameContainingIgnoreCase(name));
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
            slug = categoryRequest.getName().toLowerCase().replaceAll("[^a-z0-9\\s-]", "").replace(" ", "-");
        }

        Category category = Category.builder()
                .name(categoryRequest.getName())
                .slug(slug)
                .description(categoryRequest.getDescription())
                .isDeleted(categoryRequest.getIsDeleted())
                .parentCategory(categoryRequest.getParentId() != null ? parentCategory : null)
                .build();
        FileMetadata fileMetadata = null;

        if (categoryRequest.getImage() != null && !categoryRequest.getImage().isEmpty()) {
            fileMetadata = socialService.uploadImage(categoryRequest.getImage());

            log.info("File metadata category created: {}", fileMetadata);

            if (fileMetadata != null && fileMetadata.getUrl() != null) {
                category.setImage(fileMetadata.getUrl());
            }
        }

        log.info("File metadata category created: {}", fileMetadata);

        if (fileMetadata != null && fileMetadata.getUrl() != null) {
            category.setImage(fileMetadata.getUrl());
        }

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

        if (categoryRequest.getImage() != null && !categoryRequest.getImage().isEmpty()) {
            if (category.getImage() != null) {
                log.info("Starting delete old image: {}", category.getImage());

                var responseS3 = socialService.deleteFile(category.getImage());

                log.info("Message from s3FeignClient: {}", responseS3);

                log.info("Deleted old image: {}", category.getImage());
            }

            FileMetadata fileMetadata = socialService.uploadImage(categoryRequest.getImage());

            log.info("File metadata new image: {}", fileMetadata);

            if (fileMetadata != null && fileMetadata.getUrl() != null)
                category.setImage(fileMetadata.getUrl());
        }

        String slug = categoryRequest.getSlug();
        if (categoryRequest.getSlug() == null || categoryRequest.getSlug().isEmpty()) {
            slug = categoryRequest.getName().toLowerCase().replaceAll("[^a-z0-9\\s-]", "").replace(" ", "-");
        }

        category.setName(categoryRequest.getName());
        category.setSlug(slug);
        category.setDescription(categoryRequest.getDescription());
        category.setIsDeleted(categoryRequest.getIsDeleted());
        category.setParentCategory(categoryRequest.getParentId() != null ? parentCategory : null);
        categoryRepository.save(category);
        return mapToCategoryResponse(category);
    }

    @Override
    public FileMetadata uploadImage(MultipartFile file, Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category not found", "id", id + ""));

        FileMetadata fileMetadata = Objects.requireNonNull(socialService.uploadImage(file));

        category.setImage(fileMetadata.getUrl());

        categoryRepository.save(category);

        return fileMetadata;
    }

    @Override
    public void updateAvailable(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category not found", "id", id + ""));
        category.setIsDeleted(!category.getIsDeleted());
        categoryRepository.save(category);
    }

    @Override
    @Transactional
    public void deleteImage(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category not found", "id", id + ""));

        if (category.getImage() != null) {
            socialService.deleteFile(category.getImage());
        }

        category.setImage(null);

        categoryRepository.save(category);

    }

    @Override
    public List<CategoryResponse> getAllCategoryNotParent() {
        return mapToCategoryResponseList(categoryRepository.findAllByParentCategoryIsNull());
    }

    @Override
    public List<CategoryResponse> getAllCategorySameParent(String slug) {
        Category category = categoryRepository.findBySlug(slug).orElseThrow(() -> new ResourceNotFoundException("Category not found", "slug", slug));
        return mapToCategoryResponseList(categoryRepository.findAllByParentId(category.getParentCategory().getId()));
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


