package com.oralie.products.service.impl;


import com.oralie.products.dto.request.BrandRequest;
import com.oralie.products.dto.response.BrandResponse;
import com.oralie.products.dto.response.ListResponse;
import com.oralie.products.exception.ResourceAlreadyExistException;
import com.oralie.products.exception.ResourceNotFoundException;
import com.oralie.products.model.Brand;
import com.oralie.products.model.s3.FileMetadata;
import com.oralie.products.repository.BrandRepository;
import com.oralie.products.service.BrandService;
import com.oralie.products.service.SocialService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {

    private static final Logger log = LoggerFactory.getLogger(BrandServiceImpl.class);

    @Value("${aws.bucket.url}")
    private String URL_BUCKET;

    private final BrandRepository brandRepository;

    private final SocialService socialService;


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
                .isActive(brandRequest.getIsActive())
                .build();

        FileMetadata fileMetadata = null;
        if (brandRequest.getImage() != null && !brandRequest.getImage().isEmpty()) {
            log.info("Creating brand image: {}", brandRequest.getImage().getOriginalFilename());

            fileMetadata = socialService.uploadImage(brandRequest.getImage());

            log.info("File metadata brand created: {}", fileMetadata);
        }

        if (fileMetadata != null && fileMetadata.getUrl() != null) {
            brand.setImage(fileMetadata.getUrl());
        }

        brandRepository.save(brand);
        return mapToBrandResponse(brand);
    }

    @Override
    public BrandResponse updateBrand(Long id, BrandRequest brandRequest) {
        Brand brand = brandRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Brand not found", "id", id + ""));
        brand.setName(brandRequest.getName());
        brand.setDescription(brandRequest.getDescription());
        brand.setIsActive(brandRequest.getIsActive());

        if (brandRequest.getImage() != null && !brandRequest.getImage().isEmpty()) {
            if (brand.getImage() != null) {
                log.info("Deleting previous update image brand: {}", brand.getImage());


                String fileName = brand.getImage().replace(URL_BUCKET, "");

                var responseS3 = socialService.deleteFile(fileName);

                log.info("Message from S3: {}", responseS3);

                log.info("Deleted previous update image brand: {}", brand.getImage());
            }

            FileMetadata fileMetadata = socialService.uploadImage(brandRequest.getImage());

            log.info("File metadata after update image brand: {}", fileMetadata);

            brand.setImage(fileMetadata.getUrl());
        }

        brandRepository.save(brand);
        return mapToBrandResponse(brand);
    }

    @Override
    @Transactional
    public void deleteBrand(Long id) {
        Brand brand = brandRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Brand not found", "id", id + ""));
        if (brandRepository.existsByIdAndProductsIsEmpty(id)) {

            if (brand.getImage() != null) {
                socialService.deleteFile(brand.getImage());
            }

            brandRepository.delete(brand);
        } else {
            throw new ResourceNotFoundException("Brand cannot be deleted as it contains products", "id", id + "");
        }
    }


    @Override
    public FileMetadata uploadImage(MultipartFile file, Long id) {
        Brand brand = brandRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Brand not found", "id", id + ""));

        FileMetadata fileMetadata = Objects.requireNonNull(socialService.uploadImage(file));

        brand.setImage(fileMetadata.getUrl());

        brandRepository.save(brand);

        return fileMetadata;
    }

    @Override
    public void updateAvailable(Long id) {
        Brand brand = brandRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Brand not found", "id", id + ""));
        brand.setIsActive(!brand.getIsActive());
        brandRepository.save(brand);
    }

    @Override
    public void deleteImage(Long id) {
        Brand brand = brandRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category not found", "id", id + ""));

        if (brand.getImage() != null) {
            socialService.deleteFile(brand.getImage());
        }

        brand.setImage(null);

        brandRepository.save(brand);
    }

    private BrandResponse mapToBrandResponse(Brand brand) {

        return BrandResponse.builder()
                .id(brand.getId())
                .name(brand.getName())
                .description(brand.getDescription())
                .image(brand.getImage())
                .isActive(brand.getIsActive())
                .build();

    }

    private List<BrandResponse> mapToBrandResponseList(List<Brand> brands) {
        return brands.stream()
                .map(this::mapToBrandResponse)
                .collect(Collectors.toList());
    }
}
