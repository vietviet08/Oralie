package com.oralie.products.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.oralie.products.dto.ProductContactDto;
import com.oralie.products.dto.request.ProductQuantityPost;
import com.oralie.products.dto.request.ProductRequest;
import com.oralie.products.dto.response.ListResponse;
import com.oralie.products.dto.response.ProductBaseResponse;
import com.oralie.products.dto.response.ProductOptionResponse;
import com.oralie.products.dto.response.ProductResponse;
import com.oralie.products.dto.response.ProductResponseES;
import com.oralie.products.service.ProductService;
import com.oralie.products.service.redis.ProductRedisService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * ProductController class provides CRUD REST API endpoints for managing
 * products.
 * <p>
 * This class handles operations like creating, reading, updating, and deleting
 * products.
 * Additionally, it includes endpoints for retrieving product-related
 * information based on
 * different criteria such as category, brand, and other properties.
 * <p>
 * Attributes:
 * - accountsContactDto: Contains contact information related to product
 * accounts.
 * - environment: Provides access to the current environment properties.
 * - build: Holds the build version of the application.
 * - productService: Service used to perform product-related operations.
 */
@Slf4j
@Tag(name = "CRUD REST APIs for Products", description = "CREATE, READ, UPDATE, DELETE Products")
@RestController
@RequiredArgsConstructor
@RequestMapping(produces = { MediaType.APPLICATION_JSON_VALUE })
public class ProductController {

  private final ProductContactDto productContactDto;

  private final Environment environment;

  @Value("${info.app.version}")
  private String build;

  private final ProductService productService;

  private final ProductRedisService productRedisService;

  // store
  @GetMapping("/store/products")
  public ResponseEntity<ListResponse<ProductResponse>> getAllProducts(
      @RequestParam(required = false, defaultValue = "0") int page,
      @RequestParam(required = false, defaultValue = "10") int size,
      @RequestParam(required = false, defaultValue = "id") String sortBy,
      @RequestParam(required = false, defaultValue = "asc") String sort,
      @RequestParam(required = false) String search,
      @RequestParam(required = false) String category) throws JsonProcessingException {

    ListResponse<ProductResponse> productResponses = productRedisService.getAllProduct(page, size, sortBy, sort,
        search);

    if (productResponses == null || productResponses.getData() == null) {
      productResponses = productService.getAllProducts(page, size, sortBy, sort, search, category);
      productRedisService.saveAllProduct(productResponses, sortBy, sort, search);
    }

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(productResponses);
  }

  @GetMapping("/store/categories")
  public ResponseEntity<ListResponse<ProductResponse>> getAllProductsByBrandName(
      @RequestParam(required = false, defaultValue = "0") int page,
      @RequestParam(required = false, defaultValue = "10") int size,
      @RequestParam(required = false, defaultValue = "id") String sortBy,
      @RequestParam(required = false, defaultValue = "asc") String sort,
      @RequestParam String category,
      @RequestParam(required = false) String brand) {
    if (brand == null || brand.isEmpty()) {
      return ResponseEntity
          .status(HttpStatus.OK)
          .body(productService.getAllProductsByCategory(page, size, sortBy, sort, category));
    }
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(productService.getAllProductsByBrand(page, size, sortBy, sort, category, brand));
  }

  @GetMapping("/store/products/id/{id}")
  public ResponseEntity<ProductResponse> getProductById(@PathVariable("id") Long id) {
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(productService.getProductById(id));
  }

  @GetMapping("/store/products/{slug}")
  public ResponseEntity<ProductResponse> getProductBySlug(@PathVariable("slug") String slug) {
    log.info("slug: {}", slug);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(productService.getProductBySlug(slug));
  }

  @GetMapping("/store/products/product-es/{id}")
  public ResponseEntity<ProductResponseES> getProductByIdES(@PathVariable("id") Long id) {
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(productService.getProductByIdES(id));
  }

  @GetMapping("/store/products/product-base/{productId}")
  public ResponseEntity<ProductBaseResponse> getProductBaseById(@PathVariable("productId") Long id) {
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(productService.getProductBaseById(id));
  }

  @GetMapping("/store/products/existingById/{productId}")
  public ResponseEntity<Boolean> existingProductByProductId(@PathVariable Long productId) {
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(productService.existingProductByProductId(productId));
  }

  @GetMapping("/store/products/top10/{productId}")
  public ResponseEntity<List<ProductResponse>> top10ProductRelatedCategory(@PathVariable("productId") Long productId,
      @RequestParam String categoryName) {
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(productService.top10ProductRelatedCategory(productId, categoryName));
  }

  @GetMapping("/store/products/top12")
  public ResponseEntity<List<ProductResponse>> top12ProductOutStandingByCategorySlug(
      @RequestParam String categorySlug) {
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(productService.top12ProductOutStandingByCategorySlug(categorySlug));
  }

  @GetMapping("/store/products/options/{id}")
  public ResponseEntity<List<ProductOptionResponse>> getProductOptionsByProductId(@PathVariable Long id) {
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(productService.getProductOptionsByProductId(id));
  }

  // dash
  @PostMapping(value = "/dash/products")
  public ResponseEntity<ProductResponse> createProduct(@ModelAttribute ProductRequest productRequest) {
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(productService.createProduct(productRequest));
  }

  @PutMapping(value = "/dash/products/{id}")
  public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long id,
      @ModelAttribute ProductRequest productRequest) {
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(productService.updateProduct(id, productRequest));
  }

  @PutMapping("/dash/products/available/{id}")
  public ResponseEntity<Void> updateAvailableProduct(@PathVariable Long id) {
    productService.updateAliveProduct(id);
    return ResponseEntity
        .status(HttpStatus.OK)
        .build();
  }

  @PutMapping("/dash/products/updateQuantity")
  public ResponseEntity<List<ProductBaseResponse>> updateQuantityProduct(
      @RequestBody List<ProductQuantityPost> productQuantityPosts) {
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(productService.updateQuantityProduct(productQuantityPosts));
  }

  @DeleteMapping("/dash/products/{id}")
  public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
    productService.deleteProduct(id);
    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .build();
  }

  // info services
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
        .body(productContactDto);
  }

}
