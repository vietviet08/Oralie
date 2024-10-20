package com.oralie.products.sevice;

import com.oralie.products.dto.request.ProductImageRequest;
import com.oralie.products.model.ProductImage;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface ProductImageService {

    Map uploadFile(ProductImageRequest productImageRequest);

    Map uploadFileSingle(MultipartFile file);

}
