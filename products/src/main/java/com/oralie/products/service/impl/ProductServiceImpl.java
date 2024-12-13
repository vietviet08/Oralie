package com.oralie.products.service.impl;

import com.oralie.products.dto.request.ProductOptionRequest;
import com.oralie.products.dto.request.ProductQuantityPost;
import com.oralie.products.dto.request.ProductRequest;
import com.oralie.products.dto.request.ProductSpecificationRequest;
import com.oralie.products.dto.response.*;
import com.oralie.products.exception.ResourceAlreadyExistException;
import com.oralie.products.exception.ResourceNotFoundException;
import com.oralie.products.model.*;
import com.oralie.products.model.s3.FileMetadata;
import com.oralie.products.repository.*;
import com.oralie.products.service.ProductService;
import com.oralie.products.service.SocialService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final SocialService socialService;

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final ProductImageRepository productImageRepository;

    @Override
    public ListResponse<ProductResponse> getAllProducts(int page, int size, String sortBy, String sort, String search,
                                                        String category) {
        Sort sortObj = sort.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sortObj);
        Page<Product> pageProducts = productRepository.findAll(pageable);
        List<Product> products = pageProducts.getContent();

        return ListResponse
                .<ProductResponse>builder()
                .data(mapToProductResponseList(products))
                .pageNo(pageProducts.getNumber())
                .pageSize(pageProducts.getSize())
                .totalElements((int) pageProducts.getTotalElements())
                .totalPages(pageProducts.getTotalPages())
                .isLast(pageProducts.isLast())
                .build();
    }

    @Override
    public ListResponse<ProductResponse> getAllProductsByCategory(int page, int size, String sortBy, String sort,
                                                                  String categorySlug) {
        Sort sortObj = sort.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sortObj);
        Page<Product> pageProducts = productRepository.findAllByCategorySlug(pageable, categorySlug);
        List<Product> products = pageProducts.getContent();

        return ListResponse
                .<ProductResponse>builder()
                .data(mapToProductResponseList(products))
                .pageNo(pageProducts.getNumber())
                .pageSize(pageProducts.getSize())
                .totalElements((int) pageProducts.getTotalElements())
                .totalPages(pageProducts.getTotalPages())
                .isLast(pageProducts.isLast())
                .build();
    }

    @Override
    public ListResponse<ProductResponse> getAllProductsByBrand(int page, int size, String sortBy, String sort,
                                                               String categorySlug, String brandSlug) {
        Sort sortObj = sort.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sortObj);
        Page<Product> pageProducts = productRepository.findAllByBrandSlug(pageable, categorySlug, brandSlug);
        List<Product> products = pageProducts.getContent();

        return ListResponse
                .<ProductResponse>builder()
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
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found", "id", id + ""));
        return mapToProductResponse(product);
    }

    @Override
    public ProductResponseES getProductByIdES(Long id) {
        ProductResponse productResponse = productRepository.findById(id)
                .map(this::mapToProductResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found", "id", id + ""));
        return mapToProductResponseES(productResponse);
    }

    @Override
    public ProductBaseResponse getProductBaseById(Long id) {
        ProductResponse productResponse = productRepository.findById(id)
                .map(this::mapToProductResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found", "id", id + ""));
        return mapToProductBaseResponse(productResponse);
    }

    @Override
    public ProductResponse getProductBySlugs(String categoryName, String slug) {
        Product product = productRepository.findBySlugs(slug, categoryName)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found", "slug", slug));
        return mapToProductResponse(product);
    }

    @Override
    public ProductResponse getProductBySlug(String slug) {
        log.info("slug: {}", slug);
        Product product = productRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found", "slug", slug));
        return mapToProductResponse(product);
    }

    @Override
    @Transactional
    public ProductResponse createProduct(ProductRequest productRequest) {

        if (productRepository.existsByName(productRequest.getName())) {
            throw new ResourceAlreadyExistException("Product already exists by name is " + productRequest.getName());
        } else if (productRepository.existsBySlug(productRequest.getSlug())) {
            throw new ResourceAlreadyExistException("Product already exists by slug is " + productRequest.getSlug());
        }
        Brand brand = brandRepository.findById(productRequest.getBrandId()).orElseThrow(
                () -> new ResourceNotFoundException("Brand not found", "id", productRequest.getBrandId() + ""));

        Product product = Product.builder()
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .price(productRequest.getPrice())
                .discount(productRequest.getDiscount())
                .brand(brand)
                .sku(productRequest.getSku().toUpperCase())
                .quantity(productRequest.getQuantity())
                .slug(StringUtils.hasText(productRequest.getSlug()) ?
                        productRequest.getSlug() :
                        productRequest.getName()
                                .toLowerCase()
                                .replaceAll("[^a-z0-9\\s-]", "")
                                .replace(" ", "-"))
                .isAvailable(productRequest.getIsAvailable())
                .isDeleted(productRequest.getIsDeleted())
                .isDiscounted(productRequest.getIsDiscounted())
                .isFeatured(productRequest.getIsFeatured())
                .isPromoted(productRequest.getIsPromoted())
                .build();

        Product productSaved = productRepository.save(product);

        log.info("Starting upload images by S3 service");
        List<ProductImage> productImageList = new ArrayList<>();
        try {
            List<FileMetadata> fileMetadataList = (productRequest.getImages() != null)
                    ? socialService.uploadImages(productRequest.getImages())
                    : Collections.emptyList();

            for (FileMetadata fileMetadata : fileMetadataList) {
                ProductImage productImage = ProductImage.builder()
                        .url(fileMetadata.getUrl())
                        .product(productSaved)
                        .name(fileMetadata.getName())
                        .type(fileMetadata.getMime())
                        .build();
                productImageList.add(productImage);
            }

        } catch (Exception e) {
            log.error("Error uploading images by S3 service: {}", e.getMessage());
        }

        log.info("Images uploaded successfully");

        productSaved.setImages(productImageList);

        List<ProductOption> productOptionList = mapToProductOptionList(productRequest.getOptions(), productSaved);
        List<ProductSpecification> productSpecificationList = mapToProductSpecificationsList(productRequest.getSpecifications(), productSaved);

        List<ProductCategory> productCategoryList = new ArrayList<>();
        for (Long id : productRequest.getCategoryIds()) {
            Category category = categoryRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found", "id", id + ""));
            ProductCategory productCategory = ProductCategory.builder()
                    .category(category)
                    .product(productSaved)
                    .build();
            productCategoryList.add(productCategory);
        }

        productSaved.setOptions(productOptionList);
        productSaved.setSpecifications(productSpecificationList);
        productSaved.setProductCategories(productCategoryList);

        return mapToProductResponse(productRepository.save(productSaved));

    }

    @Override
    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest productRequest) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found", "id", id + ""));

        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setDiscount(productRequest.getDiscount());
        product.setBrand(brandRepository.findById(productRequest.getBrandId()).orElseThrow(
                () -> new ResourceNotFoundException("Brand not found", "id", productRequest.getBrandId() + "")));
        product.setSku(productRequest.getSku());
        product.setQuantity(productRequest.getQuantity());
        product.setSlug(productRequest.getSlug() != null ?
                productRequest.getSlug() :
                product.getName()
                        .toLowerCase()
                        .replaceAll("[^a-z0-9\\s-]", "")
                        .replace(" ", "-"));
        product.setIsAvailable(productRequest.getIsAvailable());
        product.setIsDeleted(productRequest.getIsDeleted());
        product.setIsDiscounted(productRequest.getIsDiscounted());
        product.setIsFeatured(productRequest.getIsFeatured());
        product.setIsPromoted(productRequest.getIsPromoted());

        Product productSaved = productRepository.save(product);

        productSaved.getOptions().clear();
        productSaved.getSpecifications().clear();

        List<ProductOption> productOptionList = mapToProductOptionList(productRequest.getOptions(), productSaved);
        List<ProductSpecification> productSpecificationList = mapToProductSpecificationsList(productRequest.getSpecifications(), productSaved);

        productSaved.getOptions().addAll(productOptionList);
        productSaved.getSpecifications().addAll(productSpecificationList);

        // check old categories if they are still in the list keep them else delete them
        // and new categories add them
//        List<ProductCategory> productCategoryListOld = product.getProductCategories();
//        List<Long> categoryIds = productRequest.getCategoryIds();
//
//        for (ProductCategory productCategory : productCategoryListOld) {
//            if (!categoryIds.contains(productCategory.getCategory().getId())) {
//                productCategoryRepository.delete(productCategory);
//            }
//        }
//
//        List<ProductCategory> productCategoryList = new ArrayList<>();
//        for (Long idCategory : productRequest.getCategoryIds()) {
//            Category category = categoryRepository.findById(idCategory)
//                    .orElseThrow(() -> new ResourceNotFoundException("Category not found", "id", idCategory + ""));
//            boolean isExist = productCategoryListOld.stream()
//                    .anyMatch(productCategory -> productCategory.getCategory().getId().equals(idCategory));
//            if (isExist) {
//                ProductCategory productCategory = ProductCategory.builder()
//                        .category(category)
//                        .product(productSaved)
//                        .build();
//                productCategoryList.add(productCategory);
//            }
//        }

        List<ProductCategory> productCategoryListOld = product.getProductCategories();
        List<Long> categoryIds = productRequest.getCategoryIds();

        productCategoryListOld.removeIf(productCategory -> {
            if (!categoryIds.contains(productCategory.getCategory().getId())) {
                productCategoryRepository.delete(productCategory);
                return true;
            }
            return false;
        });

        List<ProductCategory> productCategoryList = new ArrayList<>();
        for (Long idCategory : productRequest.getCategoryIds()) {
            Category category = categoryRepository.findById(idCategory)
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found", "id", idCategory + ""));
            boolean isExist = productCategoryListOld.stream()
                    .anyMatch(productCategory -> productCategory.getCategory().getId().equals(idCategory));
            if (!isExist) {
                ProductCategory productCategory = ProductCategory.builder()
                        .category(category)
                        .product(productSaved)
                        .build();
                productCategoryList.add(productCategory);
            }
        }

        productCategoryRepository.saveAll(productCategoryList);

        // check old images if they are still in the list keep them else delete them and
        // new images add them
        List<ProductImage> productImageListOld = product.getImages();
        List<MultipartFile> productImageNew = productRequest.getImages();

        if (productImageNew != null) {

            List<FileMetadata> fileMetadataList = socialService.uploadImages(productImageNew);

            List<String> newImageUrls = Objects.requireNonNull(fileMetadataList)
                    .stream()
                    .map(FileMetadata::getUrl)
                    .toList();

            if (productImageListOld != null && !productImageListOld.isEmpty()) {
                productImageListOld.removeIf(oldImage -> {
                    if (!newImageUrls.contains(oldImage.getUrl())) {
                        productImageRepository.delete(oldImage);
                        log.info("Deleting image by S3 service");
                        try {
                            socialService.deleteFile(oldImage.getUrl());
                        } catch (Exception e) {
                            log.error("Error deleting image by S3 service: {}", e.getMessage());
                        }
                        log.info("Image deleted successfully");
                        return true;
                    }
                    return false;
                });
            }

            assert productImageListOld != null;
            for (FileMetadata newImageUrl : fileMetadataList) {
                boolean exists = productImageListOld.stream()
                        .anyMatch(oldImage -> oldImage.getUrl().equals(newImageUrl.getUrl()));
                if (!exists) {
                    ProductImage newImage = ProductImage.builder()
                            .url(newImageUrl.getUrl())
                            .product(product)
                            .name(newImageUrl.getName())
                            .type(newImageUrl.getMime())
                            .build();
                    productImageListOld.add(newImage);
                }
            }
        }

        assert productImageListOld != null;
        productImageRepository.saveAll(productImageListOld);
        productSaved.setImages(productImageListOld);

        productSaved.setProductCategories(productCategoryList);

        return mapToProductResponse(productRepository.save(productSaved));
    }

    @Override
    public List<ProductBaseResponse> updateQuantityProduct(List<ProductQuantityPost> productQuantityPosts) {
        for (ProductQuantityPost productQuantityPost : productQuantityPosts) {
            Product product = productRepository.findById(productQuantityPost.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found", "id", productQuantityPost.getId() + ""));
            product.setQuantity(product.getQuantity() + productQuantityPost.getQuantity());
            productRepository.save(product);
        }
        return mapToProductBaseResponseList(productRepository.findAllById(productQuantityPosts.stream()
                .map(ProductQuantityPost::getId)
                .collect(Collectors.toList())));
    }

    private List<ProductBaseResponse> mapToProductBaseResponseList(List<Product> allById) {
        return allById.stream()
                .map(this::mapToProductBaseResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void updateAliveProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found", "id", id + ""));
        product.setIsAvailable(!product.getIsAvailable() ? Boolean.TRUE : Boolean.FALSE);
        product.setIsDeleted(!product.getIsDeleted() ? Boolean.TRUE : Boolean.FALSE);
        productRepository.save(product);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found", "id", id + ""));
        productRepository.delete(product);
    }

    @Override
    public boolean existingProductByProductId(Long productId) {
        return productRepository.existsById(productId);
    }

    @Override
    public List<ProductResponse> top10ProductRelatedCategory(Long productId, String categoryName) {
        return mapToProductResponseList(productRepository.getTop10ProductRelatedByCategory(productId, categoryName));
    }

    @Override
    public List<ProductOptionResponse> getProductOptionsByProductId(Long id) {
        return productRepository.findById(id)
                .map(product -> mapToProductOptionResponseList(product.getOptions()))
                .orElseThrow(() -> new ResourceNotFoundException("Product not found", "id", id + ""));
    }

    private List<ProductResponse> mapToProductResponseList(List<Product> products) {
        return products.stream()
                .map(product -> ProductResponse.builder()
                        .id(product.getId())
                        .name(product.getName())
                        .description(product.getDescription())
                        .price(product.getPrice())
                        .discount(product.getDiscount())
                        .productCategories(mapToProductCategoryResponseList(product.getProductCategories()))
                        .brand(mapToBrandResponse(product.getBrand()))
                        .sku(product.getSku())
                        .images(mapToProductImageResponseList(product.getImages()))
                        .options(mapToProductOptionResponseList(product.getOptions()))
                        .specifications(product.getSpecifications().stream()
                                .map(productSpecification -> ProductSpecificationResponse.builder()
                                        .id(productSpecification.getId())
                                        .name(productSpecification.getName())
                                        .value(productSpecification.getValue())
                                        .build())
                                .collect(Collectors.toList()))
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
                .productCategories(mapToProductCategoryResponseList(product.getProductCategories()))
                .brand(mapToBrandResponse(product.getBrand()))
                .sku(product.getSku())
                .images(mapToProductImageResponseList(product.getImages()))
                .options(mapToProductOptionResponseList(product.getOptions()))
                .specifications(product.getSpecifications().stream()
                        .map(productSpecification -> ProductSpecificationResponse.builder()
                                .id(productSpecification.getId())
                                .name(productSpecification.getName())
                                .value(productSpecification.getValue())
                                .build())
                        .collect(Collectors.toList()))
                .quantity(product.getQuantity())
                .slug(product.getSlug())
                .isAvailable(product.getIsAvailable())
                .isDeleted(product.getIsDeleted())
                .isDiscounted(product.getIsDiscounted())
                .isFeatured(product.getIsFeatured())
                .isPromoted(product.getIsPromoted())
                .build();
    }

    private BrandResponse mapToBrandResponse(Brand brand) {
        return BrandResponse.builder()
                .id(brand.getId())
                .name(brand.getName())
                .description(brand.getDescription())
                .image(brand.getImage())
                .isActive(brand.getIsActive())
                .build();
    }

    private CategoryResponse mapToCategoryResponse(Category category) {
        Long parentCategoryId = (category.getParentCategory() != null) ? category.getParentCategory().getId() : null;
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .slug(category.getSlug())
                .description(category.getDescription())
                .image(category.getImage())
                .parentId(parentCategoryId)
                .isDeleted(category.getIsDeleted())
                .build();
    }

    private List<ProductOptionResponse> mapToProductOptionResponseList(List<ProductOption> options) {
        return options.stream()
                .map(productOption -> ProductOptionResponse.builder()
                        .id(productOption.getId())
                        .name(productOption.getName())
                        .value(productOption.getValue())
                        .build())
                .collect(Collectors.toList());
    }

    private List<ProductOption> mapToProductOptionList(List<ProductOptionRequest> productOptionRequests,
                                                       Product product) {
        return productOptionRequests.stream()
                .map(productOptionRequest -> ProductOption.builder()
                        .name(productOptionRequest.getName())
                        .value(productOptionRequest.getValue())
                        .product(product)
                        .build())
                .collect(Collectors.toList());
    }

    private List<ProductSpecificationResponse> mapToProductSpecificationResponseList(List<ProductSpecification> specifications) {
        return specifications.stream()
                .map(productSpecification -> ProductSpecificationResponse.builder()
                        .id(productSpecification.getId())
                        .name(productSpecification.getName())
                        .value(productSpecification.getValue())
                        .build())
                .collect(Collectors.toList());
    }

    private List<ProductSpecification> mapToProductSpecificationsList(List<ProductSpecificationRequest> productSpecificationRequests,
                                                                      Product product) {
        return productSpecificationRequests.stream()
                .map(productSpecificationRequest -> ProductSpecification.builder()
                        .name(productSpecificationRequest.getName())
                        .value(productSpecificationRequest.getValue())
                        .product(product)
                        .build())
                .collect(Collectors.toList());
    }

    private List<ProductCategoryResponse> mapToProductCategoryResponseList(List<ProductCategory> productCategories) {
        return productCategories.stream()
                .map(productCategory -> ProductCategoryResponse.builder()
                        .id(productCategory.getId())
                        .name(productCategory.getCategory().getName())
                        .category(mapToCategoryResponse(productCategory.getCategory()))
                        .idProduct(productCategory.getProduct().getId())
                        .build())
                .collect(Collectors.toList());
    }

    private List<ProductImageResponse> mapToProductImageResponseList(List<ProductImage> productImages) {
        return productImages.stream()
                .map(productImage -> ProductImageResponse.builder()
                        .id(productImage.getId())
                        .name(productImage.getName())
                        .url(productImage.getUrl())
                        .type(productImage.getType())
                        .build())
                .collect(Collectors.toList());
    }

    private List<ProductImage> mapToProductImageList(List<ProductImageResponse> productImageRequests, Product product) {
        return productImageRequests.stream()
                .map(productImageRequest -> ProductImage.builder()
                        .id(productImageRequest.getId())
                        .name(productImageRequest.getName())
                        .type(productImageRequest.getType())
                        .url(productImageRequest.getUrl())
                        .product(product)
                        .build())
                .collect(Collectors.toList());
    }

    private ProductResponseES mapToProductResponseES(ProductResponse productResponse) {
        return ProductResponseES.builder()
                .id(productResponse.getId())
                .productName(productResponse.getName())
                .slug(productResponse.getSlug())
                .categories(productResponse.getProductCategories().stream().map(ProductCategoryResponse::getName).collect(Collectors.toList()))
                .options(productResponse.getOptions().stream().map(ProductOptionResponse::getName).collect(Collectors.toList()))
                .brand(productResponse.getBrand().getName())
                .sku(productResponse.getSku())
                .description(productResponse.getDescription())
                .price(productResponse.getPrice())
                .discount(productResponse.getDiscount())
                .quantity(productResponse.getQuantity())
                .isDiscounted(productResponse.getIsDiscounted())
                .isAvailable(productResponse.getIsAvailable())
                .isDeleted(productResponse.getIsDeleted())
                .isFeatured(productResponse.getIsFeatured())
                .isPromoted(productResponse.getIsPromoted())
                .build();
    }

    private ProductBaseResponse mapToProductBaseResponse(Product product) {
        return ProductBaseResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .options(mapToProductOptionResponseList(product.getOptions()))
                .slug(product.getSlug())
                .image((product.getImages() != null && !product.getImages().isEmpty())
                        ? product.getImages().get(0).getUrl()
                        : null)
                .price(product.getPrice())
                .discount(product.getDiscount())
                .brand(product.getBrand().getId())
                .category((product.getProductCategories() != null)
                        ? product.getProductCategories().stream().map(ProductCategory::getId).collect(Collectors.toList())
                        : Collections.emptyList())
                .quantity(product.getQuantity())
                .isDiscounted(product.getIsDiscounted())
                .isAvailable(product.getIsAvailable())
                .isDeleted(product.getIsDeleted())
                .isFeatured(product.getIsFeatured())
                .isPromoted(product.getIsPromoted())
                .build();
    }

    private ProductBaseResponse mapToProductBaseResponse(ProductResponse productResponse) {
        return ProductBaseResponse.builder()
                .id(productResponse.getId())
                .name(productResponse.getName())
                .description(productResponse.getDescription())
                .options(productResponse.getOptions())
                .slug(productResponse.getSlug())
                .image((productResponse.getImages() != null && !productResponse.getImages().isEmpty())
                        ? productResponse.getImages().get(0).getUrl()
                        : null)
                .price(productResponse.getPrice())
                .discount(productResponse.getDiscount())
                .brand(productResponse.getBrand().getId())
                .category((productResponse.getProductCategories() != null)
                        ? productResponse.getProductCategories().stream().map(ProductCategoryResponse::getId).collect(Collectors.toList())
                        : Collections.emptyList()).quantity(productResponse.getQuantity())
                .isDiscounted(productResponse.getIsDiscounted())
                .isAvailable(productResponse.getIsAvailable())
                .isDeleted(productResponse.getIsDeleted())
                .isFeatured(productResponse.getIsFeatured())
                .isPromoted(productResponse.getIsPromoted())
                .build();
    }
}
