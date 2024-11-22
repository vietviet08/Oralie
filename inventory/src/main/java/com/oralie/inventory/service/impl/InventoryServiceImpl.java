package com.oralie.inventory.service.impl;

import com.oralie.inventory.dto.request.InventoryRequest;
import com.oralie.inventory.dto.response.ProductBaseResponse;
import com.oralie.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {


    @Override
    public void addProductToWareHouse(Long wareHouseId, Long productId, int quantity) {
        
    }

    @Override
    public ProductBaseResponse updateProductQuantity(InventoryRequest inventoryRequest) {
        return null;
    }
}
