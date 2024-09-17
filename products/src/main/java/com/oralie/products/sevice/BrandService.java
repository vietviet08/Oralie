package com.oralie.products.sevice;

import com.oralie.products.dto.request.BrandRequest;
import com.oralie.products.dto.response.BrandResponse;

import java.util.List;

public interface BrandService {
    List<BrandResponse> getAllBrands();
    BrandResponse getBrandById(Long id);
    BrandResponse createBrand(BrandRequest brandRequest);
    BrandResponse updateBrand(Long id, BrandRequest brandRequest);
    void deleteBrand(Long id);

}
