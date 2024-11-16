package com.oralie.products.service.impl;

import com.oralie.products.dto.CommonTypeCloudinary;
import com.oralie.products.dto.request.ProductImageRequest;
import com.oralie.products.dto.response.ProductImageResponse;
import com.oralie.products.exception.ResourceNotFoundException;
import com.oralie.products.model.Product;
import com.oralie.products.model.ProductImage;
import com.oralie.products.repository.ProductImageRepository;
import com.oralie.products.repository.ProductRepository;
import com.oralie.products.service.CloudinaryService;
import com.oralie.products.service.ProductImageService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductImageServiceImpl implements ProductImageService {

    private final ProductRepository productRepository;
    private final CloudinaryService cloudinaryService;
    private final ProductImageRepository productImageRepository;
    private static final String FOLDER_NAME = "products";
    private static final Logger log = LoggerFactory.getLogger(ProductImageServiceImpl.class);

    @Override
    public List<ProductImageResponse> uploadFile(ProductImageRequest productImageRequest, Long productId) {

        try {

            Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product not found", "id", productId.toString()));

            List<MultipartFile> files = productImageRequest.getFile();
            List<String> urls = cloudinaryService.uploadFile(files, FOLDER_NAME);
            List<ProductImage> productImages = new ArrayList<>();
            List<CommonTypeCloudinary> commonTypeCloudinaries = new ArrayList<>();

            for (MultipartFile file : files) {
                CommonTypeCloudinary commonTypeCloudinary = CommonTypeCloudinary.builder()
                        .name(file.getOriginalFilename())
                        .type(file.getContentType())
                        .build();
                commonTypeCloudinaries.add(commonTypeCloudinary);
            }

            int index = 0;
            for (String url : urls) {
                ProductImage productImage = ProductImage.builder()
                        .name(commonTypeCloudinaries.get(index).getName())
                        .type(commonTypeCloudinaries.get(index).getType())
                        .url(url)
                        .product(product)
                        .build();
                productImages.add(productImage);
                log.info("Uploaded file: {}", url);
                index++;
            }

            productImageRepository.saveAll(productImages);

            return mapProductImageToResponse(productImages);

        } catch (Exception e) {
            log.error("Error uploading file: {}", e.getMessage());
            return null;
        }

    }

    @Override
    public Map uploadFileSingle(MultipartFile file) {

        try {
            String url = cloudinaryService.uploadFileSingle(file, FOLDER_NAME);
            ProductImage productImage = ProductImage.builder()
                    .name(file.getOriginalFilename())
                    .type(file.getContentType())
                    .url(url).build();
            productImageRepository.save(productImage);
            Map<Object, Object> response = new HashMap<>();
            log.info("Uploaded file: {}", url);
            response.put("url", url);

            return response;
        } catch (Exception e) {
            log.error("Error uploading file: {}", e.getMessage());
            return null;
        }
    }

    private List<ProductImageResponse> mapProductImageToResponse(List<ProductImage> productImages) {
        List<ProductImageResponse> productImageResponses = new ArrayList<>();
        for (ProductImage productImage : productImages) {
            ProductImageResponse productImageResponse = ProductImageResponse.builder()
                    .id(productImage.getId())
                    .name(productImage.getName())
                    .type(productImage.getType())
                    .url(productImage.getUrl())
                    .build();
            productImageResponses.add(productImageResponse);
        }
        return productImageResponses;
    }

}
