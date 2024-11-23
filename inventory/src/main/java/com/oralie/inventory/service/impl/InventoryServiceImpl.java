package com.oralie.inventory.service.impl;

import com.oralie.inventory.dto.request.InventoryRequest;
import com.oralie.inventory.dto.response.ProductBaseResponse;
import com.oralie.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {
    
    private final InventoryRepository inventoryRepository;    
    private final WareHouseRepository warehouseRepository;
    private final ProductService productService;
    
    @Override
    public void addProductToWareHouse(List<InventoryRequest> inventoryRequest) {
        //check request exist or not in warehouse
        List<Inventory> listInventory = inventoryRequest.stream().map(
            inventory -> {
            
                boolean existingInventory = inventoryRepository.existsByWarehouseIdAndProductId(inventory.getWareHouse().getId(), inventory.getProductId());
                
                if(existingInventory) throw new ResourceAlreadyExistException("inventory", "id", inventory.getId().toString()); 
                
                WareHouse warehouse = warehouseRepostory.findById(inventory.getWareHouseId()).orElseThrow(() -> new ResourceNotFoundException("warehouse", "warehouseId", inventory.getWareHouseId.toString()));
                
                ProductBaseResponse product = productService.getProduct(inventory.getProductId());
                
                if(product == null) throw new ResourceNotFoundException("product", "productId", inventory.getProductId().toString()); 
                
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
        List<Invetory> inventories = inventoryRepository.findAllById(inventoryQuantityRequests.stream().map(InventoryQuantityRequest::inventoryId).toList());
        
        //update quantity in inventory
        for(Inventory inventory : inventories){
            InventoryQuantityRequest iqr = inventoryQuantityRequests.stream().filter(request -> request.getInvetoryId().equal(invetory.getId())).findFirst().orElse(null);
            if(iqr != null){
                if(iqr.getQuantity() < 0) throw new BadRequestException(InventoryConstant.INVALID_VALUE_REQUEST);
                Long quantityToSet = iqr.getQuantity() != null ? iqr.getQuantity() : 0;    
                
                invetory.setQuantity(quantityToSet);
                
            }
            
        }
        inventoryRepository.saveAll(inventories);
        
        List<ProductQuantityPost> productQuantityPosts = mapListInventoryToProductQuantityPost(inventories);
        
        List<ProductBaseResponse> productBaseResponses = new ArraryList<>;
        if(productQuantityPosts != null)
            productBaseResponses = productService.updateProductQuantity(productQuantityPosts);
    
        return productBaseResponses;
    }
    
    private List<ProductQuantityPost> mapListInventoryToProductQuantityPost(List<Inventory> inventories){
       
       List<ProductQuantityPost> productQuantityPosts = inventories.stream().map(
              inventory -> {
                  return ProductQuantityPost.builder()
                                      .productId(invetory.getProductId())
                                      .quantity(inventory.getQuantity())
                                      .build();
              }
        ).toList();
        
        return productQuantityPosts;
    }
}
