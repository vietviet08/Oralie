package com.oralie.inventory.service.impl;

import com.oralie.inventory.constant.InventoryConstant;
import com.oralie.inventory.dto.request.InventoryQuantityRequest;
import com.oralie.inventory.dto.request.InventoryRequest;
import com.oralie.inventory.dto.request.ProductQuantityPost;
import com.oralie.inventory.dto.response.InventoryResponse;
import com.oralie.inventory.dto.response.ListResponse;
import com.oralie.inventory.dto.response.ProductBaseResponse;
import com.oralie.inventory.exception.ResourceAlreadyExistException;
import com.oralie.inventory.exception.ResourceNotFoundException;
import com.oralie.inventory.model.Inventory;
import com.oralie.inventory.model.WareHouse;
import com.oralie.inventory.repository.InventoryRepository;
import com.oralie.inventory.repository.WareHouseRepository;
import com.oralie.inventory.service.InventoryService;
import com.oralie.inventory.service.ProductService;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final WareHouseRepository warehouseRepository;
    private final ProductService productService;

    @Override
    public ListResponse<InventoryResponse> getAllInventories(int page, int size, String sortBy, String sort, String search) {
        Sort sortObj = sort.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sortObj);

        Page<Inventory> pageInventory;

        if (search != null && !search.isEmpty()) {
            pageInventory = inventoryRepository.findByProductNameContainingIgnoreCase(search, pageable);
        } else {
            pageInventory = inventoryRepository.findAll(pageable);
        }

        List<Inventory> inventories = pageInventory.getContent();

        return ListResponse.<InventoryResponse>builder()
                .data(mapToInventoryResponseList(inventories))
                .pageNo(pageInventory.getNumber())
                .pageSize(pageInventory.getSize())
                .totalElements((int) pageInventory.getTotalElements())
                .totalPages(pageInventory.getTotalPages())
                .isLast(pageInventory.isLast())
                .build();
    }

    @Override
    public void addProductToWareHouse(List<InventoryRequest> inventoryRequest) {
        //check request exist or not in warehouse
        List<Inventory> listInventory = inventoryRequest.stream().map(
                inventory -> {

                    boolean existingInventory = inventoryRepository.existsByWarehouseIdAndProductId(inventory.getWareHouseId(), inventory.getProductId());

                    if (existingInventory)
                        throw new ResourceAlreadyExistException(InventoryConstant.ALREADY_EXISTING_RESOURCE + ": inventory", "id" + inventory.getProductId().toString());

                    WareHouse warehouse = warehouseRepository.findById(inventory.getWareHouseId()).orElseThrow(() -> new ResourceNotFoundException("warehouse", "warehouseId", inventory.getWareHouseId().toString()));

                    ProductBaseResponse product = productService.getProduct(inventory.getProductId());

                    if (product == null)
                        throw new ResourceNotFoundException(InventoryConstant.RESOURCE_NOT_FOUND + " :product", "productId", inventory.getProductId().toString());

                    return Inventory.builder()
                            .productId(inventory.getProductId())
                            .productName(product.getName())
                            .quantity(0L)
                            .wareHouse(warehouse)
                            .build();
                }
        ).toList();

        inventoryRepository.saveAll(listInventory);

    }

    @Override
    public List<ProductBaseResponse> restockProduct(List<InventoryQuantityRequest> inventoryQuantityRequests) {
        //get list inventory from this request
        List<Inventory> inventories = inventoryRepository.findAllById(inventoryQuantityRequests.stream().map(InventoryQuantityRequest::getInventoryId).toList());

        //update quantity in inventory
        for (Inventory inventory : inventories) {
            InventoryQuantityRequest iqr = inventoryQuantityRequests.stream().filter(request -> request.getInventoryId().equals(inventory.getId())).findFirst().orElse(null);
            if (iqr != null) {
                if (iqr.getQuantity() < 0) throw new BadRequestException(InventoryConstant.INVALID_VALUE_REQUEST);
                inventory.setQuantity(iqr.getQuantity());
            }
        }
        inventoryRepository.saveAll(inventories);

        List<ProductQuantityPost> productQuantityPosts = mapListInventoryToProductQuantityPost(inventories);

        List<ProductBaseResponse> productBaseResponses = null;
        if (productQuantityPosts != null)
            productBaseResponses = productService.updateProductQuantity(productQuantityPosts);

        return productBaseResponses;
    }

    @Override
    public void reduceProductQuantity(InventoryQuantityRequest inventoryQuantityRequests) {
        if(inventoryQuantityRequests.getQuantity() < 0) throw new BadRequestException(InventoryConstant.INVALID_VALUE_REQUEST);
        Inventory inventory = inventoryRepository
                .findById(inventoryQuantityRequests.getInventoryId()).orElseThrow(() -> new ResourceNotFoundException("inventory", "inventoryId", inventoryQuantityRequests.getInventoryId().toString()));
        inventory.setQuantity(inventory.getQuantity() - inventoryQuantityRequests.getQuantity());
        inventoryRepository.save(inventory);
    }

    @Override
    public boolean checkProductQuantity(Long productId) {
        Inventory inventory = inventoryRepository
                .findByProductId(productId).orElseThrow(() -> new ResourceNotFoundException("inventory", "productId", productId.toString()));
        return inventory.getQuantity() > 0;
    }

    @Override
    public void reserveProductQuantity(ProductQuantityPost productQuantityPost) {
        Inventory inventory = inventoryRepository
                .findByProductId(productQuantityPost
                        .getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("inventory", "productId", productQuantityPost.getProductId().toString()));
        if(inventory.getQuantity() < productQuantityPost.getQuantity()) throw new BadRequestException(InventoryConstant.INVALID_VALUE_REQUEST);
        inventory.setQuantity(inventory.getQuantity() - productQuantityPost.getQuantity());
        inventoryRepository.save(inventory);
    }

    @Override
    public void releaseProductQuantity(ProductQuantityPost productQuantityPost) {
        Inventory inventory = inventoryRepository
                .findByProductId(productQuantityPost
                        .getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("inventory", "productId", productQuantityPost.getProductId().toString()));
        inventory.setQuantity(inventory.getQuantity() + productQuantityPost.getQuantity());
        inventoryRepository.save(inventory);
    }

    private List<ProductQuantityPost> mapListInventoryToProductQuantityPost(List<Inventory> inventories) {

        return inventories.stream().map(
                inventory -> {
                    return ProductQuantityPost.builder()
                            .productId(inventory.getProductId())
                            .quantity(inventory.getQuantity())
                            .build();
                }
        ).toList();
    }

    private List<InventoryResponse> mapToInventoryResponseList(List<Inventory> inventories) {
        return inventories.stream().map(
                inventory -> {
                    return InventoryResponse.builder()
                            .id(inventory.getId())
                            .productId(inventory.getProductId())
                            .productName(inventory.getProductName())
                            .quantity(inventory.getQuantity())
                            .wareHouseId(inventory.getWareHouse().getId())
                            .build();
                }
        ).toList();
    }
}
