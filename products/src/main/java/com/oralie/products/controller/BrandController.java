package com.oralie.products.controller;

import com.netflix.discovery.converters.Auto;
import com.oralie.products.dto.request.BrandRequest;
import com.oralie.products.dto.response.BrandResponse;
import com.oralie.products.dto.response.ListResponse;
import com.oralie.products.sevice.BrandService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "CRUD REST APIs for Brand",
        description = "CREATE, READ, UPDATE, DELETE Brand"
)
@RestController
@RequestMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
public class BrandController {

    @Autowired
    private BrandService brandService;

    @GetMapping("/dash/brands")
    public ResponseEntity<ListResponse<BrandResponse>> getAllBrands(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sort
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(brandService.getAllBrands(page, size, sortBy, sort));
    }

    @GetMapping("/dash/brands/{id}")
    public ResponseEntity<BrandResponse> getBrandById(@PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(brandService.getBrandById(id));
    }

    @PostMapping("/dash/brands")
    public ResponseEntity<BrandResponse> createBrand(@RequestBody BrandRequest brandRequest) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(brandService.createBrand(brandRequest));
    }

    @PutMapping("/dash/brands/{id}")
    public ResponseEntity<BrandResponse> updateBrand(@PathVariable Long id, @RequestBody BrandRequest brandRequest) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(brandService.updateBrand(id, brandRequest));
    }

    @DeleteMapping("/dash/brands/{id}")
    public ResponseEntity<Void> deleteBrand(@PathVariable Long id) {
        brandService.deleteBrand(id);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

}
