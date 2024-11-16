package com.oralie.products.service.impl;

import com.oralie.products.dto.request.ProductOptionRequest;
import com.oralie.products.dto.response.ListResponse;
import com.oralie.products.dto.response.ProductOptionResponse;
import com.oralie.products.exception.ResourceNotFoundException;
import com.oralie.products.model.ProductOption;
import com.oralie.products.repository.ProductOptionRepository;
import com.oralie.products.service.ProductOptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductOptionServiceImpl implements ProductOptionService {

    private final ProductOptionRepository productOptionRepository;

    @Override
    public ListResponse<ProductOptionResponse> getAllProductOptions(int page, int size, String sortBy, String sort) {
        Sort sortObj = sort.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sortObj);
        Page<ProductOption> pageProductOption = productOptionRepository.findAll(pageable);
        List<ProductOption> productOptions = pageProductOption.getContent();

        return ListResponse.<ProductOptionResponse>builder()
                .data(mapToProductOptionResponseList(productOptions))
                .pageNo(pageProductOption.getNumber())
                .pageSize(pageProductOption.getSize())
                .totalElements((int) pageProductOption.getTotalElements())
                .totalPages(pageProductOption.getTotalPages())
                .isLast(pageProductOption.isLast())
                .build();
    }


    @Override
    public ProductOptionResponse getProductOptionById(Long id) {
        ProductOption productOption = productOptionRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product option not found", "id", id + ""));
        return mapToProductOptionResponse(productOption);

    }

    @Override
    public ProductOptionResponse createProductOption(ProductOptionRequest productOptionRequest) {
        ProductOption productOption = ProductOption.builder()
                .name(productOptionRequest.getName())
                .value(productOptionRequest.getValue())
                .build();
        productOptionRepository.save(productOption);
        return mapToProductOptionResponse(productOption);
    }

    @Override
    public ProductOptionResponse updateProductOption(Long id, ProductOptionRequest productOptionRequest) {
        ProductOption productOption = productOptionRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product option not found", "id", id + ""));
        productOption.setName(productOptionRequest.getName());
        productOption.setValue(productOptionRequest.getValue());
        return mapToProductOptionResponse(productOption);
    }

    @Override
    public void deleteProductOption(Long id) {
        ProductOption productOption = productOptionRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product option not found", "id", id + ""));
        productOptionRepository.delete(productOption);
    }

    private ProductOptionResponse mapToProductOptionResponse(ProductOption productOption) {
        return ProductOptionResponse.builder()
                .id(productOption.getId())
                .name(productOption.getName())
                .build();
    }

    private List<ProductOptionResponse> mapToProductOptionResponseList(List<ProductOption> productOptions) {
        return productOptions.stream()
                .map(this::mapToProductOptionResponse)
                .collect(Collectors.toList());
    }
}
