package com.oralie.products.sevice.impl;


import com.oralie.products.dto.request.BrandRequest;
import com.oralie.products.dto.response.BrandResponse;
import com.oralie.products.exception.ResourceNotFoundException;
import com.oralie.products.model.Brand;
import com.oralie.products.repository.BrandRepository;
import com.oralie.products.sevice.BrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;

    @Override
    public List<BrandResponse> getAllBrands(int page, int size, String sortBy, String sort) {
        Sort sortObj = sort.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sortObj);

        List<Brand> brands = brandRepository.findAll(pageable).getContent();

        return mapToBrandResponseList(brands);
    }

    @Override
    public BrandResponse getBrandById(Long id) {
        return mapToBrandResponse(brandRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Brand not found", "id", id + "")));
    }

    @Override
    public BrandResponse createBrand(BrandRequest brandRequest) {
        Brand brand = Brand.builder()
                .name(brandRequest.getName())
                .description(brandRequest.getDescription())
                .imageUrl(brandRequest.getUrlImage())
                .isActive(brandRequest.getIsActive())
                .build();
        brandRepository.save(brand);
        return mapToBrandResponse(brand);
    }

    @Override
    public BrandResponse updateBrand(Long id, BrandRequest brandRequest) {
        Brand brand = brandRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Brand not found", "id", id + ""));
        brand.setName(brandRequest.getName());
        brand.setDescription(brandRequest.getDescription());
        brand.setImageUrl(brandRequest.getUrlImage());
        brand.setIsActive(brandRequest.getIsActive());
        brandRepository.save(brand);
        return mapToBrandResponse(brand);
    }

    @Override
    public void deleteBrand(Long id) {
        Brand brand = brandRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Brand not found", "id", id + ""));
        brandRepository.delete(brand);
    }

    private BrandResponse mapToBrandResponse(Brand brand) {

        return BrandResponse.builder()
                .id(brand.getId())
                .name(brand.getName())
                .description(brand.getDescription())
                .urlImage(brand.getImageUrl())
                .isActive(brand.getIsActive())
                .build();

    }

    private List<BrandResponse> mapToBrandResponseList(List<Brand> brands) {
        return brands.stream()
                .map(this::mapToBrandResponse)
                .collect(Collectors.toList());
    }
}
