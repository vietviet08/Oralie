package com.oralie.inventory.controller;

import com.oralie.inventory.dto.request.InventoryQuantityRequest;
import com.oralie.inventory.dto.request.InventoryRequest;
import com.oralie.inventory.dto.request.ProductQuantityPost;
import com.oralie.inventory.dto.response.InventoryResponse;
import com.oralie.inventory.dto.response.ListResponse;
import com.oralie.inventory.dto.response.ProductBaseResponse;
import com.oralie.inventory.service.InventoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Tag(
        name = "The API of Inventory Service",
        description = "This API provides operations for Inventory Service"
)
@RestController
@RequiredArgsConstructor
@RequestMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/dash/inventory")
    public ResponseEntity<ListResponse<InventoryResponse>> getAllWareHouses(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sort,
            @RequestParam(required = false) String search
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(inventoryService.getAllInventories(page, size, sortBy, sort, search));
    }

    @PostMapping("/dash/inventory")
    public ResponseEntity<Void> addProductToInventory(@RequestBody List<InventoryRequest> inventoryRequest) {
        inventoryService.addProductToWareHouse(inventoryRequest);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/dash/inventory/restock")
    public ResponseEntity<List<ProductBaseResponse>> restockProduct(@RequestBody List<InventoryQuantityRequest> inventoryQuantityRequests) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(inventoryService.restockProduct(inventoryQuantityRequests));
    }

    @PostMapping("/dash/inventory/reduce")
    public ResponseEntity<Void> reduceProductQuantity(@RequestBody InventoryQuantityRequest inventoryQuantityRequest) {
        inventoryService.reduceProductQuantity(inventoryQuantityRequest);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/dash/inventory/check/{productId}")
    public ResponseEntity<Boolean> checkProductQuantity(@PathVariable Long productId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(inventoryService.checkProductQuantity(productId));
    }

    @PostMapping("/dash/inventory/reserve")
    public ResponseEntity<Void> reserveProductQuantity(@RequestBody ProductQuantityPost productQuantityPost) {
        inventoryService.reserveProductQuantity(productQuantityPost);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/dash/inventory/release")
    public ResponseEntity<Void> releaseProductQuantity(@RequestBody ProductQuantityPost productQuantityPost) {
        inventoryService.releaseProductQuantity(productQuantityPost);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
