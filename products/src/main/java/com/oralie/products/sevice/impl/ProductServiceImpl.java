package com.oralie.products.sevice.impl;

import com.oralie.products.dto.request.ProductImageRequest;
import com.oralie.products.dto.request.ProductOptionRequest;
import com.oralie.products.dto.request.ProductRequest;
import com.oralie.products.dto.response.*;
import com.oralie.products.exception.ResourceAlreadyExistException;
import com.oralie.products.exception.ResourceNotFoundException;
import com.oralie.products.model.*;
import com.oralie.products.model.s3.FileMetadata;
import com.oralie.products.repository.*;
import com.oralie.products.repository.client.S3FeignClient;
import com.oralie.products.sevice.ProductImageService;
import com.oralie.products.sevice.ProductService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final ProductOptionRepository productOptionRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductImageService productImageService;

    @Qualifier("com.oralie.products.repository.client.S3FeignClient")
    private final S3FeignClient s3FeignClient;

    @Override
    public ListResponse<ProductResponse> getAllProducts(int page, int size, String sortBy, String sort, String search, String category) {
        Sort sortObj = sort.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
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
    public ListResponse<ProductResponse> getAllProductsByCategory(int page, int size, String sortBy, String sort, String categoryName) {
        Sort sortObj = sort.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sortObj);
        Page<Product> pageProducts = productRepository.findAllByCategoryName(pageable, categoryName);
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
    public ListResponse<ProductResponse> getAllProductsByBrand(int page, int size, String sortBy, String sort, String categoryName, String brandName) {
        Sort sortObj = sort.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sortObj);
        Page<Product> pageProducts = productRepository.findAllByBrandName(pageable, categoryName, brandName);
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
        Product product = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found", "id", id + ""));
        return mapToProductResponse(product);
    }

    @Override
    public ProductResponse getProductBySlugs(String categoryName, String slug) {
        Product product = productRepository.findBySlugs(slug, categoryName).orElseThrow(() -> new ResourceNotFoundException("Product not found", "slug", slug));
        return mapToProductResponse(product);
    }

    @Override
    public ProductResponse getProductBySlug(String slug) {
        Product product = productRepository.findBySlug(slug).orElseThrow(() -> new ResourceNotFoundException("Product not found", "slug", slug));
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
        Brand brand = brandRepository.findById(productRequest.getBrandId()).orElseThrow(() -> new ResourceNotFoundException("Brand not found", "id", productRequest.getBrandId() + ""));

        Product product = Product.builder()
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .price(productRequest.getPrice())
                .discount(productRequest.getDiscount())
//                .productCategories(productCategoryList)
                .brand(brand)
                .sku(productRequest.getSku())
//                .images(productImageList)
//                .options(mapToProductOptionList(productRequest.getOptions()))
                .quantity(productRequest.getQuantity())
                .slug(productRequest.getSlug())
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

            var fileMetadataList = s3FeignClient.uploadImages(productRequest.getImages());

            log.info("Status s3 service after upload images: {}", fileMetadataList.getStatusCode());

            if (fileMetadataList.getBody() != null) {
                for (FileMetadata fileMetadata : fileMetadataList.getBody()) {
                    ProductImage productImage = ProductImage.builder()
                            .url(fileMetadata.getUrl())
                            .product(productSaved)
                            .name(fileMetadata.getName())
                            .type(fileMetadata.getMime())
                            .build();
                    productImageList.add(productImage);
                }
            }

        } catch (Exception e) {
            log.error("Error uploading images by S3 service: {}", e.getMessage());
        }

        log.info("Images uploaded successfully");

        productSaved.setImages(productImageList);

//        List<ProductImageResponse> productImageResponses = productImageService.uploadFile(productRequest.getImages(), productSaved.getId());
//        productSaved.setImages(mapToProductImageList(productImageResponses, productSaved));

        List<ProductOption> productOptionList = mapToProductOptionList(productRequest.getOptions(), productSaved);

        List<ProductCategory> productCategoryList = new ArrayList<>();
        for (Long id : productRequest.getCategoryIds()) {
            Category category = categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category not found", "id", id + ""));
            ProductCategory productCategory = ProductCategory.builder()
                    .category(category)
                    .product(productSaved)
                    .build();
            productCategoryList.add(productCategory);
        }

//        List<ProductImage> productImageList = new ArrayList<>();
//        if (productImageSaved != null) {
//            for (String urlImage : productImageSaved.stream().map(ProductImage::getUrl).toList()) {
//                ProductImage productImage = ProductImage.builder()
//                        .url(urlImage)
//                        .product(productSaved)
//                        .name("Image" + productRequest.getImagesUrl().indexOf(urlImage))
//                        .type("image")
//                        .build();
//                productImageList.add(productImage);
//            }
//        }
//
        //s3 service
//        if (productRequest.getImages() != null) {
//            List<MultipartFile> images = productRequest.getImages();
//            List<FileMetadata> fileMetadataList = s3FeignClient.createAttachments(images).getBody();
//
//            List<ProductImage> productImageList = new ArrayList<>();
//            for (FileMetadata fileMetadata : fileMetadataList) {
//                ProductImage productImage = ProductImage.builder()
//                        .url(fileMetadata.getUrl())
//                        .product(productSaved)
//                        .name(fileMetadata.getName())
//                        .type(fileMetadata.getMime())
//                        .build();
//                productImageList.add(productImage);
//            }
//            productSaved.setImages(productImageList);
//        }

        productSaved.setOptions(productOptionList);
        productSaved.setProductCategories(productCategoryList);
//        productSaved.setImages(productImageList);

        return mapToProductResponse(productRepository.save(productSaved));

    }

    @Override
    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest productRequest) {

        Product product = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found", "id", id + ""));

        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setDiscount(productRequest.getDiscount());
//        product.setProductCategories(productCategoryList);
        product.setBrand(brandRepository.findById(productRequest.getBrandId()).orElseThrow(() -> new ResourceNotFoundException("Brand not found", "id", productRequest.getBrandId() + "")));
        product.setSku(productRequest.getSku());
//        product.setImages(productImageList);
//        product.setOptions(mapToProductOptionList(productRequest.getOptions()));
        product.setQuantity(productRequest.getQuantity());
        product.setSlug(productRequest.getSlug());
        product.setIsAvailable(productRequest.getIsAvailable());
        product.setIsDeleted(productRequest.getIsDeleted());
        product.setIsDiscounted(productRequest.getIsDiscounted());
        product.setIsFeatured(productRequest.getIsFeatured());
        product.setIsPromoted(productRequest.getIsPromoted());

        Product productSaved = productRepository.save(product);

        List<ProductOption> productOptionList = mapToProductOptionList(productRequest.getOptions(), productSaved);

        //check old categories if they are still in the list keep them else delete them and new categories add them
        List<ProductCategory> productCategoryListOld = product.getProductCategories();
        List<Long> categoryIds = productRequest.getCategoryIds();

        for (ProductCategory productCategory : productCategoryListOld) {
            if (!categoryIds.contains(productCategory.getCategory().getId())) {
                productCategoryRepository.delete(productCategory);
            }
        }

        List<ProductCategory> productCategoryList = new ArrayList<>();
        for (Long idCategory : productRequest.getCategoryIds()) {
            Category category = categoryRepository.findById(idCategory).orElseThrow(() -> new ResourceNotFoundException("Category not found", "id", idCategory + ""));
            boolean isExist = productCategoryListOld.stream().anyMatch(productCategory -> productCategory.getCategory().getId().equals(idCategory));
            if (isExist) {
                ProductCategory productCategory = ProductCategory.builder()
                        .category(category)
                        .product(productSaved)
                        .build();
                productCategoryList.add(productCategory);
            }
        }

        productCategoryRepository.saveAll(productCategoryList);

        //check old images if they are still in the list keep them else delete them and new images add them
        List<ProductImage> productImageListOld = product.getImages();
        List<MultipartFile> productImageNew = productRequest.getImages();


        List<ProductImage> productImageList = new ArrayList<>();
        if (productImageNew != null) {
            //            List<String> newImageUrls = productImageService.uploadFile(productImageNew, product.getId())
//                    .stream()
//                    .map(ProductImageResponse::getUrl)
//                    .toList();

            List<FileMetadata> fileMetadataList = s3FeignClient.createAttachments(productImageNew).getBody();

            List<String> newImageUrls = Objects.requireNonNull(fileMetadataList)
                    .stream()
                    .map(FileMetadata::getUrl)
                    .toList();

            for (ProductImage oldImage : productImageListOld) {
                if (!newImageUrls.contains(oldImage.getUrl())) {
                    productImageRepository.delete(oldImage);
                    log.info("Deleting image by S3 service");
                    try {
                        s3FeignClient.deleteFile(oldImage.getUrl());
                    } catch (Exception e) {
                        log.error("Error deleting image by S3 service: {}", e.getMessage());
                    }
                    log.info("Image deleted successfully");
                } else {
                    productImageList.add(oldImage);
                }
            }

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
                    productImageList.add(newImage);
                }
            }
        }

//        List<String> imagesUrl = productRequest.getImagesUrl();
//
//        for (ProductImage productImage : productImageListOld) {
//            if (!imagesUrl.contains(productImage.getUrl())) {
//                productImageRepository.delete(productImage);
//            }
//        }
//        List<ProductImage> productImageList = new ArrayList<>();
//        if (productRequest.getImagesUrl() != null) {
//            for (String urlImage : productRequest.getImagesUrl()) {
//                boolean isExist = productImageListOld.stream().anyMatch(productImage -> productImage.getUrl().equals(urlImage));
//                if (isExist) {
//                    ProductImage productImage = ProductImage.builder()
//                            .url(urlImage)
//                            .product(productSaved)
//                            .name("Image" + productRequest.getImagesUrl().indexOf(urlImage))
//                            .type("image")
//                            .build();
//                    productImageList.add(productImage);
//                }
//            }
//        }

        productImageRepository.saveAll(productImageList);

        productSaved.setOptions(productOptionList);
        productSaved.setProductCategories(productCategoryList);
        productSaved.setImages(productImageList);

        return mapToProductResponse(productRepository.save(productSaved));
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
                        .productCategories(mapToProductCategoryResponseList(product.getProductCategories()))
                        .brand(mapToBrandResponse(product.getBrand()))
                        .sku(product.getSku())
                        .images(mapToProductImageResponseList(product.getImages()))
                        .options(mapToProductOptionResponseList(product.getOptions()))
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

    private List<ProductOption> mapToProductOptionList(List<ProductOptionRequest> productOptionRequests, Product product) {
        return productOptionRequests.stream()
                .map(productOptionRequest -> ProductOption.builder()
                        .name(productOptionRequest.getName())
                        .value(productOptionRequest.getValue())
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
}
