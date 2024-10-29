package com.oralie.products.sevice;

import com.oralie.products.dto.request.ProductImageRequest;
import com.oralie.products.dto.response.ProductImageResponse;
import com.oralie.products.model.ProductImage;
import com.oralie.products.model.s3.FileMetadata;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface ProductImageService {

    //cloudinary
    List<ProductImageResponse> uploadFile(ProductImageRequest productImageRequest, Long productId);
    Map uploadFileSingle(MultipartFile file);

    //s3


}
