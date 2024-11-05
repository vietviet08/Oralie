package com.oralie.products.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oralie.products.dto.ProductContactDto;
import com.oralie.products.dto.request.ProductOptionRequest;
import com.oralie.products.dto.request.ProductRequest;
import com.oralie.products.dto.response.ListResponse;
import com.oralie.products.dto.response.ProductResponse;
import com.oralie.products.dto.response.ProductResponseES;
import com.oralie.products.sevice.ProductImageService;
import com.oralie.products.sevice.ProductService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

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

    private final ProductImageService productImageService;

    @GetMapping("/store/products")
    public ResponseEntity<ListResponse<ProductResponse>> getAllProducts(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sort,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(productService.getAllProducts(page, size, sortBy, sort, search, category));
    }

    @GetMapping("/store/categories")
    public ResponseEntity<ListResponse<ProductResponse>> getAllProductsByBrandName(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sort,
            @RequestParam(required = false) String categoryName,
            @RequestParam(required = false) String brandName
    ) {
        if (brandName == null) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(productService.getAllProductsByCategory(page, size, sortBy, sort, categoryName));
        }
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(productService.getAllProductsByBrand(page, size, sortBy, sort, categoryName, brandName));
    }

    @GetMapping("/store/products/id/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable("id") Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(productService.getProductById(id));
    }


    @GetMapping("/store/products/{slug}")
    public ResponseEntity<ProductResponse> getProductBySlug(@PathVariable("slug") String slug) {
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

    // dash
    @PostMapping(value = "/dash/products", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ProductResponse> createProduct(@ModelAttribute ProductRequest productRequest) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(productService.createProduct(productRequest));
    }

    @GetMapping("/dash/products/product-es/{id}")
    public ResponseEntity<ProductResponseES> getProductByIdES(@PathVariable("id") Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(productService.getProductByIdES(id));
    }

    @GetMapping("/dash/products/product-base/{productId}")
    public ResponseEntity<ProductResponseES> getProductBaseById(@PathVariable("productId") Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(productService.getProductByIdES(id));
    }


    @PutMapping(value = "/dash/products/{id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long id, @ModelAttribute ProductRequest productRequest) {
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

    //info services
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
                .body("JAVA_HOME: " + environment.getProperty("JAVA_HOME")
                        + "\n JAVA_VERSION: " + System.getProperty("java.version")
                        + "\n JAVA_VENDOR: " + System.getProperty("java.vendor")
                        + "\n JAVA_VM: " + System.getProperty("java.vm.name")
                        + "\n JAVA_CATALINA: " + environment.getProperty("CATALINA_HOME"));
    }

    @GetMapping("/products/contact-info")
    public ResponseEntity<ProductContactDto> getProductsContactDto() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(accountsContactDto);
    }


}
