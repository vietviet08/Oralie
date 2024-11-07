package com.oralie.products.controller;

import com.oralie.products.model.ProductSpecification;
import com.oralie.products.sevice.ProductSpecificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(
        name = "APIs for ProductSpecification",
        description = "ProductSpecification CRUD APIs"
)
@RestController
@RequiredArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class ProductSpecificationController {

    private final ProductSpecificationService productSpecificationService;

    @Operation(
            summary = "Get product specification",
            description = "REST APIs get product specification in Product Specification Controller"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Get product specification successfully - Http status OK"
    )
    @GetMapping("/store/products/product-specifications/{id}")
    public ResponseEntity<Map<String, String>> getSpecifications(@PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(productSpecificationService.getProductSpecifications(id));
    }

    @Operation(
            summary = "Create product specification",
            description = "REST APIs create product specification in Product Specification Controller"
    )
    @ApiResponse(
            responseCode = "201",
            description = "Create product specification successfully - Http status CREATED"
    )
    @PostMapping("/dash/products/product-specifications/{id}")
    public ResponseEntity<Map<String, String>> saveSpecifications(@PathVariable Long id, @RequestBody Map<String, String> specifications) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(productSpecificationService.saveProductSpecification(id, specifications));
    }

    @Operation(
            summary = "Update product specification",
            description = "REST APIs update product specification in Product Specification Controller"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Update product specification successfully - Http status OK"
    )
    @PutMapping("/dash/products/product-specifications/{id}")
    public ResponseEntity<Map<String, String>> updateSpecifications(@PathVariable Long id, @RequestBody Map<String, String> specifications) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(productSpecificationService.updateProductSpecification(id, specifications));
    }

    @Operation(
            summary = "Delete product specification",
            description = "REST APIs delete all product specification by id product specification in Product Specification Controller"
    )
    @ApiResponse(
            responseCode = "204",
            description = "Get product specification successfully - Http status NO_CONTENT"
    )
    @DeleteMapping("/dash/products/product-specifications/{id}")
    public ResponseEntity<Void> deleteSpecifications(@PathVariable Long id) {
        productSpecificationService.deleteProductSpecification(id);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @Operation(
            summary = "Delete product specification",
            description = "REST APIs delete product specification by specification name in Product Specification Controller"
    )
    @ApiResponse(
            responseCode = "204",
            description = "Get product specification successfully - Http status NO_CONTENT"
    )
    @DeleteMapping("/dash/products/product-specifications")
    public ResponseEntity<Void> deleteSpecification(@RequestParam Long id, @RequestParam String specificationName) {
        productSpecificationService.deleteProductSpecification(id, specificationName);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }


}
