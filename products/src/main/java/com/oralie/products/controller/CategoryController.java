package com.oralie.products.controller;

import com.oralie.products.dto.request.CategoryRequest;
import com.oralie.products.dto.response.CategoryResponse;
import com.oralie.products.dto.response.ListResponse;
import com.oralie.products.sevice.CategoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/dash/categories")
    public ResponseEntity<CategoryResponse> createCategory(@RequestBody CategoryRequest categoryRequest) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(categoryService.createCategory(categoryRequest));
    }

    @PutMapping("/dash/categories/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(@PathVariable Long id, @RequestBody CategoryRequest categoryRequest) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(categoryService.updateCategory(id, categoryRequest));
    }

    @DeleteMapping("/dash/categories/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}
