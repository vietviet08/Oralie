package com.oralie.products.sevice.impl;

import com.oralie.products.dto.request.ProductImageRequest;
import com.oralie.products.model.ProductImage;
import com.oralie.products.repository.ProductImageRepository;
import com.oralie.products.sevice.CloudinaryService;
import com.oralie.products.sevice.ProductImageService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductImageImpl implements ProductImageService {

    private final CloudinaryService cloudinaryService;
    private final ProductImageRepository productImageRepository;
    private static final Logger log = LoggerFactory.getLogger(ProductImageImpl.class);

    @Override
    public Map uploadFile(ProductImageRequest productImageRequest) {
        try {
            List<MultipartFile> files = productImageRequest.getFile();
            List<String> urls = cloudinaryService.uploadFile(files, "products");
            for (String url : urls) {
                ProductImage productImage = ProductImage.builder()
                        .name(productImageRequest.getName())
                        .type(productImageRequest.getType())
                        .url(url).build();
                productImageRepository.save(productImage);
            }
            Map<Object, Object> response = new HashMap<>();
            for (String url : urls) {
                log.info("Uploaded file: {}", url);
                response.put("url", url);
            }
            return response;

        } catch (Exception e) {
            log.error("Error uploading file: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public Map uploadFileSingle(MultipartFile file) {
        try {
            String url = cloudinaryService.uploadFileSingle(file, "products");
//            ProductImage productImage = ProductImage.builder()
//                    .name(file.getOriginalFilename())
//                    .type(file.getContentType())
//                    .url(url).build();
//            productImageRepository.save(productImage);
            Map<Object, Object> response = new HashMap<>();
            log.info("Uploaded file: {}", url);
            response.put("url", url);
            return response;
        } catch (Exception e) {
            log.error("Error uploading file: {}", e.getMessage());
            return null;
        }
    }
}
