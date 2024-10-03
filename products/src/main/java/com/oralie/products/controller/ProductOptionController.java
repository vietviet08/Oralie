package com.oralie.products.controller;

import com.oralie.products.dto.request.ProductOptionRequest;
import com.oralie.products.dto.response.ListResponse;
import com.oralie.products.dto.response.ProductOptionResponse;
import com.oralie.products.sevice.ProductOptionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "CRUD REST APIs for ProductOption",
        description = "CREATE, READ, UPDATE, DELETE ProductOption"
)
@RestController
@RequestMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
public class ProductOptionController {

    @Autowired
    private ProductOptionService productOptionService;

    @GetMapping("/dash/product-options")
    public ResponseEntity<ListResponse<ProductOptionResponse>> getAllProductOptions(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sort
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(productOptionService.getAllProductOptions(page, size, sortBy, sort));
    }

    @GetMapping("/dash/product-options/{id}")
    public ResponseEntity<ProductOptionResponse> getProductOptionById(@PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(productOptionService.getProductOptionById(id));
    }

    @PostMapping("/dash/product-options")
    public ResponseEntity<ProductOptionResponse> createProductOption(@RequestBody ProductOptionRequest productOptionRequest) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(productOptionService.createProductOption(productOptionRequest));
    }

    @PutMapping("/dash/product-options/{id}")
    public ResponseEntity<ProductOptionResponse> updateProductOption(@PathVariable Long id, @RequestBody ProductOptionRequest productOptionRequest) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(productOptionService.updateProductOption(id, productOptionRequest));
    }

    @DeleteMapping("/dash/product-options/{id}")
    public ResponseEntity<Void> deleteProductOption(@PathVariable Long id) {
        productOptionService.deleteProductOption(id);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}
