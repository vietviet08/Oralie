package com.oralie.inventory.service;

public interface InventoryService {
    void addProductToWareHouse(Long wareHouseId, Long productId, int quantity);
    
}
