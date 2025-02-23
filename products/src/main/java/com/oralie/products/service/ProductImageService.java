package com.oralie.products.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.oralie.products.dto.request.ProductImageRequest;
import com.oralie.products.dto.response.ProductImageResponse;

public interface ProductImageService {

  // cloudinary
  List<ProductImageResponse> uploadFile(ProductImageRequest productImageRequest, Long productId);

  Map<Object, Object> uploadFileSingle(MultipartFile file);

  // s3

}
