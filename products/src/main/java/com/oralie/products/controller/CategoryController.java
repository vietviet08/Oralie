package com.oralie.products.controller;

import com.oralie.products.dto.request.CategoryRequest;
import com.oralie.products.dto.response.CategoryResponse;
import com.oralie.products.dto.response.ListResponse;
import com.oralie.products.model.s3.FileMetadata;
import com.oralie.products.service.CategoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(
        name = "CRUD REST APIs for Category",
        description = "CREATE, READ, UPDATE, DELETE Category"
)
@RestController
@RequiredArgsConstructor
@RequestMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/dash/categories")
    public ResponseEntity<ListResponse<CategoryResponse>> getAllCategories(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sort
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(categoryService.getAllCategories(page, size, sortBy, sort));
    }

    @GetMapping("/dash/categories/{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(categoryService.getCategoryById(id));
    }

    @PostMapping(value = "/dash/categories")
    public ResponseEntity<CategoryResponse> createCategory(@ModelAttribute CategoryRequest categoryRequest) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(categoryService.createCategory(categoryRequest));
    }

    @PutMapping(value = "/dash/categories/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(@PathVariable Long id, @ModelAttribute CategoryRequest categoryRequest) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(categoryService.updateCategory(id, categoryRequest));
    }

    @PutMapping("/dash/categories/available/{id}")
    public ResponseEntity<Void> updateAvailableCategory(@PathVariable Long id) {
        categoryService.updateAvailable(id);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @DeleteMapping("/dash/categories/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @PostMapping("/store/categories/upload-image")
    public ResponseEntity<FileMetadata> uploadImage(@RequestParam("image") MultipartFile file, @RequestParam("id") Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(categoryService.uploadImage(file, id));
    }

    @DeleteMapping("/store/categories/delete-image/{id}")
    public ResponseEntity<Void> deleteImage(@PathVariable Long id) {
        categoryService.deleteImage(id);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}
