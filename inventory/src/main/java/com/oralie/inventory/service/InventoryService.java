package com.oralie.inventory.service;

import com.oralie.inventory.dto.request.InventoryRequest;
import com.oralie.inventory.dto.response.ProductBaseResponse;

public interface InventoryService {

    void addProductToWareHouse(List<InventoryRequest>  inventoryRequest);

    List<ProductBaseResponse> updateProductQuantity(List<InventoryQuantityRequest> inventoryQuantityRequests);
}
