package com.oralie.products.controller;

import com.oralie.products.dto.ProductContactDto;
import com.oralie.products.dto.request.ProductRequest;
import com.oralie.products.dto.response.ListResponse;
import com.oralie.products.dto.response.ProductResponse;
import com.oralie.products.sevice.ProductService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "CRUD REST APIs for Products",
        description = "CREATE, READ, UPDATE, DELETE Products"
)
@RestController
@RequiredArgsConstructor
@RequestMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
public class ProductController {

    private final ProductContactDto accountsContactDto;

    private final Environment environment;

    @Value("${info.app.version}")
    private String build;

    private final ProductService productService;

    @GetMapping("/store/products")
    public ResponseEntity<ListResponse<ProductResponse>> getAllProducts(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sort
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(productService.getAllProducts(page, size, sortBy, sort));
    }


    @GetMapping("/store/{categoryName}")
    public ResponseEntity<ListResponse<ProductResponse>> getAllProductsByBrandName(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sort,
            @PathVariable String categoryName,
            @RequestParam(required = false) String brandName
    ) {
        if(brandName == null) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(productService.getAllProductsByCategory(page, size, sortBy, sort, categoryName));
        }
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(productService.getAllProductsByBrand(page, size, sortBy, sort, categoryName, brandName));
    }

    @GetMapping("/store/products/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(productService.getProductById(id));
    }


    @GetMapping("/store/product/{slug}")
    public ResponseEntity<ProductResponse> getProductBySlug(@PathVariable String slug) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(productService.getProductBySlug(slug));
    }

    @GetMapping("/store/{categoryName}/{slug}")
    public ResponseEntity<ProductResponse> getProductBySlugs(
            @PathVariable String categoryName,
            @PathVariable String slug
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(productService.getProductBySlugs(categoryName, slug));
    }

//    @GetMapping("/store/products/{id}/options")
//    public ResponseEntity<ListResponse<ProductOptionResponse>> getProductOptions(@PathVariable Long id) {
//        return ResponseEntity
//                .status(HttpStatus.OK)
//                .body(productService.getProductOptions(id));
//    }

    @PostMapping("/dash/products")
    public ResponseEntity<ProductResponse> createProduct(@RequestBody ProductRequest productRequest) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(productService.createProduct(productRequest));
    }

    @PutMapping("/dash/products/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long id, @RequestBody ProductRequest productRequest) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(productService.updateProduct(id, productRequest));
    }

    @DeleteMapping("/dash/products/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @GetMapping("/products/build-version")
    public ResponseEntity<String> getBuildVersion() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(build);
    }

    @GetMapping("/products/java-version")
    public ResponseEntity<String> getJavaVersion() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(environment.getProperty("JAVA_HOME"));
    }


    @GetMapping("/products/contact-info")
    public ResponseEntity<ProductContactDto> getProductsContactDto() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(accountsContactDto);
    }


}
