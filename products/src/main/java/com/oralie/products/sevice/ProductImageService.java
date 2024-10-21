package com.oralie.products.sevice;

import com.oralie.products.dto.request.ProductImageRequest;
import com.oralie.products.dto.response.ProductImageResponse;
import com.oralie.products.model.ProductImage;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface ProductImageService {

    List<ProductImageResponse> uploadFile(ProductImageRequest productImageRequest, Long productId);

    Map uploadFileSingle(MultipartFile file);

}
