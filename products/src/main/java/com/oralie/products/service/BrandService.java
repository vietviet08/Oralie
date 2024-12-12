package com.oralie.products.service;

import com.oralie.products.dto.request.BrandRequest;
import com.oralie.products.dto.response.BrandResponse;
import com.oralie.products.dto.response.ListResponse;
import com.oralie.products.model.s3.FileMetadata;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BrandService {
    ListResponse<BrandResponse> getAllBrands(int page, int size, String sortBy, String sort, String search);

    List<BrandResponse> getAllBrands();

    BrandResponse getBrandById(Long id);

    BrandResponse createBrand(BrandRequest brandRequest);

    BrandResponse updateBrand(Long id, BrandRequest brandRequest);

    void deleteBrand(Long id);

    FileMetadata uploadImage(MultipartFile file, Long id);

    void updateAvailable(Long id);

    void deleteImage(Long id);
}
