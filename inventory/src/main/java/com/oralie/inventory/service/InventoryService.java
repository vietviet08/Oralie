package com.oralie.inventory.service;

import com.oralie.inventory.dto.request.InventoryRequest;
import com.oralie.inventory.dto.response.ProductBaseResponse;

public interface InventoryService {

    void addProductToWareHouse(Long wareHouseId, Long productId, int quantity);

    ProductBaseResponse updateProductQuantity(InventoryRequest inventoryRequest);
}
