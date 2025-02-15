package com.oralie.inventory.service;

import com.oralie.inventory.dto.request.InventoryQuantityRequest;
import com.oralie.inventory.dto.request.InventoryRequest;
import com.oralie.inventory.dto.request.ProductQuantityPost;
import com.oralie.inventory.dto.response.InventoryResponse;
import com.oralie.inventory.dto.response.ListResponse;
import com.oralie.inventory.dto.response.ProductBaseResponse;

import java.util.List;

public interface InventoryService {

    void test();

    ListResponse<InventoryResponse> getAllInventories(int page, int size, String sortBy, String sort, String search);

    void addProductToWareHouse(List<InventoryRequest>  inventoryRequest);

    List<ProductBaseResponse> restockProduct(List<InventoryQuantityRequest> inventoryQuantityRequests);

    void reduceProductQuantity(InventoryQuantityRequest inventoryQuantityRequests);

    boolean checkProductQuantity(Long productId);

    void reserveProductQuantity(List<ProductQuantityPost> productQuantityPosts);

    void releaseProductQuantity(List<ProductQuantityPost> productQuantityPosts);

}
