package com.oralie.inventory.controller;

import com.oralie.inventory.dto.request.WareHouseRequest;
import com.oralie.inventory.dto.response.ListResponse;
import com.oralie.inventory.dto.response.WareHouseResponse;
import com.oralie.inventory.service.WareHouseService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "The API of WareHouse Service",
        description = "This API provides operations for WareHouse Service"
)
@RestController
@RequiredArgsConstructor
@RequestMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
public class WareHouseController {

    private final WareHouseService wareHouseService;

    @GetMapping("/dash/warehouses")
    public ResponseEntity<ListResponse<WareHouseResponse>> getAllWareHouses(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sort,
            @RequestParam(required = false) String search
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(wareHouseService.getAllWareHouses(page, size, sortBy, sort, search));
    }

    @GetMapping("/dash/warehouses/{id}")
    public ResponseEntity<WareHouseResponse> getWareHouseById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(wareHouseService.getWareHouseById(id));
    }

    @PostMapping("/dash/warehouses")
    public ResponseEntity<WareHouseResponse> createWareHouse(@RequestBody WareHouseRequest wareHouseRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(wareHouseService.createWareHouse(wareHouseRequest));
    }

    @PutMapping("/dash/warehouses/{id}")
    public ResponseEntity<WareHouseResponse> updateWareHouse(@PathVariable Long id, @RequestBody WareHouseRequest wareHouseRequest) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(wareHouseService.updateWareHouse(id, wareHouseRequest));
    }

    @DeleteMapping("/dash/warehouses/{id}")
    public ResponseEntity<Void> deleteWareHouse(@PathVariable Long id) {
        wareHouseService.deleteWareHouse(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
