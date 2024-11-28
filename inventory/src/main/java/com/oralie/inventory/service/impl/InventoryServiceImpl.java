package com.oralie.inventory.service.impl;

import com.oralie.inventory.constant.InventoryConstant;
import com.oralie.inventory.dto.request.InventoryQuantityRequest;
import com.oralie.inventory.dto.request.InventoryRequest;
import com.oralie.inventory.dto.request.ProductQuantityPost;
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
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private static final Logger log = LoggerFactory.getLogger(InventoryServiceImpl.class);
    private final InventoryRepository inventoryRepository;
    private final WareHouseRepository warehouseRepository;
    private final ProductService productService;

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
    public List<ProductBaseResponse> updateProductQuantity(List<InventoryQuantityRequest> inventoryQuantityRequests) {
        //get list inventory from this request
        List<Inventory> inventories = inventoryRepository.findAllById(inventoryQuantityRequests.stream().map(InventoryQuantityRequest::getInventoryId).toList());

        //update quantity in inventory
        for (Inventory inventory : inventories) {
            InventoryQuantityRequest iqr = inventoryQuantityRequests.stream().filter(request -> request.getInventoryId().equals(inventory.getId())).findFirst().orElse(null);
            if (iqr != null) {
                if (iqr.getQuantity() < 0) throw new BadRequestException(InventoryConstant.INVALID_VALUE_REQUEST);
                Long quantityToSet = iqr.getQuantity() != null ? iqr.getQuantity() : 0;

                inventory.setQuantity(quantityToSet);

            }

        }
        inventoryRepository.saveAll(inventories);

        List<ProductQuantityPost> productQuantityPosts = mapListInventoryToProductQuantityPost(inventories);

        List<ProductBaseResponse> productBaseResponses = null;
        if (productQuantityPosts != null)
            productBaseResponses = productService.updateProductQuantity(productQuantityPosts);

        return productBaseResponses;
    }

    private List<ProductQuantityPost> mapListInventoryToProductQuantityPost(List<Inventory> inventories) {

        List<ProductQuantityPost> productQuantityPosts = inventories.stream().map(
                inventory -> {
                    return ProductQuantityPost.builder()
                            .productId(inventory.getProductId())
                            .quantity(inventory.getQuantity())
                            .build();
                }
        ).toList();

        return productQuantityPosts;
    }
}
