package com.oralie.products.sevice.impl;

import com.oralie.products.dto.request.ProductRequest;
import com.oralie.products.dto.response.ListResponse;
import com.oralie.products.dto.response.ProductResponse;
import com.oralie.products.exception.ResourceNotFoundException;
import com.oralie.products.model.Product;
import com.oralie.products.repository.*;
import com.oralie.products.sevice.ProductService;
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
public class ProductServiceImpl  implements ProductService{

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final ProductOptionRepository productOptionRepository;
    private final ProductImageRepository productImageRepository;

    @Override
    public ListResponse<ProductResponse> getAllProducts(int page, int size, String sortBy, String sort) {
        Sort sortObj = sort.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sortObj);
        Page<Product> pageProducts = productRepository.findAll(pageable);
        List<Product> products = pageProducts.getContent();

        return ListResponse.<ProductResponse>builder()
                .data(mapToProductResponseList(products))
                .pageNo(pageProducts.getNumber())
                .pageSize(pageProducts.getSize())
                .totalElements((int) pageProducts.getTotalElements())
                .totalPages(pageProducts.getTotalPages())
                .isLast(pageProducts.isLast())
                .build();
    }

    @Override
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found", "id", id + ""));
        return mapToProductResponse(product);
    }

    @Override
    public ProductResponse createProduct(ProductRequest productRequest) {
        Product product = Product.builder()
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .price(productRequest.getPrice())
                .discount(productRequest.getDiscount())
                .productCategories(productRequest.getProductCategories())
                .brand(productRequest.getBrand())
                .sku(productRequest.getSku())
                .images(productRequest.getImages())
                .options(productRequest.getOptions())
                .quantity(productRequest.getQuantity())
                .slug(productRequest.getSlug())
                .isAvailable(productRequest.getIsAvailable())
                .isDeleted(productRequest.getIsDeleted())
                .isDiscounted(productRequest.getIsDiscounted())
                .isFeatured(productRequest.getIsFeatured())
                .isPromoted(productRequest.getIsPromoted())
                .build();
        return mapToProductResponse(productRepository.save(product));
    }

    @Override
    public ProductResponse updateProduct(Long id, ProductRequest productRequest) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found", "id", id + ""));
        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setDiscount(productRequest.getDiscount());
        product.setProductCategories(productRequest.getProductCategories());
        product.setBrand(productRequest.getBrand());
        product.setSku(productRequest.getSku());
        product.setImages(productRequest.getImages());
        product.setOptions(productRequest.getOptions());
        product.setQuantity(productRequest.getQuantity());
        product.setSlug(productRequest.getSlug());
        product.setIsAvailable(productRequest.getIsAvailable());
        product.setIsDeleted(productRequest.getIsDeleted());
        product.setIsDiscounted(productRequest.getIsDiscounted());
        product.setIsFeatured(productRequest.getIsFeatured());
        product.setIsPromoted(productRequest.getIsPromoted());
        return mapToProductResponse(productRepository.save(product));
    }

    @Override
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found", "id", id + ""));
        productRepository.delete(product);
    }

    private List<ProductResponse> mapToProductResponseList(List<Product> products) {
        return products.stream()
                .map(product -> ProductResponse.builder()
                        .id(product.getId())
                        .name(product.getName())
                        .description(product.getDescription())
                        .price(product.getPrice())
                        .discount(product.getDiscount())
                        .productCategories(product.getProductCategories())
                        .brand(product.getBrand())
                        .sku(product.getSku())
                        .images(product.getImages())
                        .options(product.getOptions())
                        .quantity(product.getQuantity())
                        .slug(product.getSlug())
                        .isAvailable(product.getIsAvailable())
                        .isDeleted(product.getIsDeleted())
                        .isDiscounted(product.getIsDiscounted())
                        .isFeatured(product.getIsFeatured())
                        .isPromoted(product.getIsPromoted())
                        .build())
                .collect(Collectors.toList());
    }

    private ProductResponse mapToProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .discount(product.getDiscount())
                .productCategories(product.getProductCategories())
                .brand(product.getBrand())
                .sku(product.getSku())
                .images(product.getImages())
                .options(product.getOptions())
                .quantity(product.getQuantity())
                .slug(product.getSlug())
                .isAvailable(product.getIsAvailable())
                .isDeleted(product.getIsDeleted())
                .isDiscounted(product.getIsDiscounted())
                .isFeatured(product.getIsFeatured())
                .isPromoted(product.getIsPromoted())
                .build();
    }
}
