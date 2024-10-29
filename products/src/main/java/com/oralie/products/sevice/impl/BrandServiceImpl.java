package com.oralie.products.sevice.impl;


import com.oralie.products.dto.request.BrandRequest;
import com.oralie.products.dto.response.BrandResponse;
import com.oralie.products.dto.response.ListResponse;
import com.oralie.products.exception.ResourceAlreadyExistException;
import com.oralie.products.exception.ResourceNotFoundException;
import com.oralie.products.model.Brand;
import com.oralie.products.model.Category;
import com.oralie.products.model.s3.FileMetadata;
import com.oralie.products.repository.BrandRepository;
import com.oralie.products.repository.client.S3FeignClient;
import com.oralie.products.sevice.BrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;

    private final S3FeignClient s3FeignClient;

    @Override
    public ListResponse<BrandResponse> getAllBrands(int page, int size, String sortBy, String sort) {
        Sort sortObj = sort.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sortObj);
        Page<Brand> pageBrands = brandRepository.findAll(pageable);
        List<Brand> brands = pageBrands.getContent();

        return ListResponse.<BrandResponse>builder()
                .data(mapToBrandResponseList(brands))
                .pageNo(pageBrands.getNumber())
                .pageSize(pageBrands.getSize())
                .totalElements((int) pageBrands.getTotalElements())
                .totalPages(pageBrands.getTotalPages())
                .isLast(pageBrands.isLast())
                .build();
    }

    @Override
    public BrandResponse getBrandById(Long id) {
        return mapToBrandResponse(brandRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Brand not found", "id", id + "")));
    }

    @Override
    public BrandResponse createBrand(BrandRequest brandRequest) {
        if (brandRepository.existsByName(brandRequest.getName()))
            throw new ResourceAlreadyExistException("Brand already exists with name " + brandRequest.getName());
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
        if (brandRepository.existsByIdAndProductsIsEmpty(id)) {
            brandRepository.delete(brand);
        } else {
            throw new ResourceNotFoundException("Brand cannot be deleted as it contains products", "id", id + "");
        }
    }


    @Override
    public FileMetadata uploadImage(MultipartFile file, Long id) {
        Brand brand = brandRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Brand not found", "id", id + ""));

        FileMetadata fileMetadata = Objects.requireNonNull(s3FeignClient.createAttachments(List.of(file)).getBody()).get(0);

        brand.setImageUrl(fileMetadata.getUrl());

        brandRepository.save(brand);

        return fileMetadata;
    }

    @Override
    public void deleteImage(Long id) {
        Brand brand = brandRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category not found", "id", id + ""));

        if (brand.getImageUrl() != null) {
            s3FeignClient.deleteFile(brand.getImageUrl());
        }

        brand.setImageUrl(null);

        brandRepository.save(brand);
    }

    private BrandResponse mapToBrandResponse(Brand brand) {

        return BrandResponse.builder()
                .id(brand.getId())
                .name(brand.getName())
                .description(brand.getDescription())
                .imageUrl(brand.getImageUrl())
                .isActive(brand.getIsActive())
                .build();

    }

    private List<BrandResponse> mapToBrandResponseList(List<Brand> brands) {
        return brands.stream()
                .map(this::mapToBrandResponse)
                .collect(Collectors.toList());
    }
}
