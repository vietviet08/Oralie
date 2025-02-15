package com.oralie.products.controller;

import com.oralie.products.dto.request.BrandRequest;
import com.oralie.products.dto.response.BrandResponse;
import com.oralie.products.dto.response.ListResponse;
import com.oralie.products.model.Brand;
import com.oralie.products.model.s3.FileMetadata;
import com.oralie.products.service.BrandService;
import com.oralie.products.utils.excel.ExcelExporter;
import com.oralie.products.utils.obj.ObjectManage;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Tag(
        name = "CRUD REST APIs for Brand",
        description = "CREATE, READ, UPDATE, DELETE Brand"
)
@RestController
@RequiredArgsConstructor
@RequestMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
public class BrandController {

    private final BrandService brandService;

    @GetMapping("/store/brands")
    public ResponseEntity<ListResponse<BrandResponse>> getAllBrands(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sort,
            @RequestParam(required = false) String search
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(brandService.getAllBrands(page, size, sortBy, sort, search));
    }

    @GetMapping("/store/brands/all")
    public ResponseEntity<List<BrandResponse>> getAllBrands() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(brandService.getAllBrands());
    }

    @GetMapping("/store/brands/{id}")
    public ResponseEntity<BrandResponse> getBrandById(@PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(brandService.getBrandById(id));
    }
    //dash
    @GetMapping("/dash/export-brands")
    public void exportBrands(HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=brands_" + currentDateTime + ".xlsx";
        response.setHeader(headerKey, headerValue);

        ExcelExporter<BrandResponse> excelExporter = new ExcelExporter<>(brandService.getAllBrands());

        List<String> fieldsToExport = List.of("id",
                "name",
                "isDeleted",
                "isActivated");
        excelExporter.export(response, ObjectManage.Brands.name(), fieldsToExport);
    }

    @PostMapping(value = "/dash/brands")
    public ResponseEntity<BrandResponse> createBrand(@ModelAttribute BrandRequest brandRequest) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(brandService.createBrand(brandRequest));
    }

    @PutMapping(value = "/dash/brands/{id}")
    public ResponseEntity<BrandResponse> updateBrand(@PathVariable Long id, @ModelAttribute BrandRequest brandRequest) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(brandService.updateBrand(id, brandRequest));
    }

    @PutMapping("/dash/brands/available/{id}")
    public ResponseEntity<Void> updateAvailableBrand(@PathVariable Long id) {
        brandService.updateAvailable(id);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @DeleteMapping("/dash/brands/{id}")
    public ResponseEntity<Void> deleteBrand(@PathVariable Long id) {
        brandService.deleteBrand(id);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @PostMapping("/store/brands/upload-image")
    public ResponseEntity<FileMetadata> uploadImage(@RequestParam("image") MultipartFile file, @RequestParam("id") Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(brandService.uploadImage(file, id));
    }

    @DeleteMapping("/store/brand/delete-image/{id}")
    public ResponseEntity<Void> deleteImage(@PathVariable Long id) {
        brandService.deleteImage(id);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

}
