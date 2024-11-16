package com.oralie.products.service;

import com.oralie.products.dto.request.ProductImageRequest;
import com.oralie.products.dto.response.ProductImageResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface ProductImageService {

    //cloudinary
    List<ProductImageResponse> uploadFile(ProductImageRequest productImageRequest, Long productId);
    Map uploadFileSingle(MultipartFile file);

    //s3


}
