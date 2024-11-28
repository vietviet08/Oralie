package com.oralie.inventory.service;

import com.oralie.inventory.dto.request.InventoryQuantityRequest;
import com.oralie.inventory.dto.request.InventoryRequest;
import com.oralie.inventory.dto.response.ProductBaseResponse;

import java.util.List;

public interface InventoryService {

    void addProductToWareHouse(List<InventoryRequest>  inventoryRequest);

    List<ProductBaseResponse> updateProductQuantity(List<InventoryQuantityRequest> inventoryQuantityRequests);
}
