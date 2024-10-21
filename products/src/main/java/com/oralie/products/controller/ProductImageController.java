package com.oralie.products.controller;

import com.oralie.products.dto.request.ProductImageRequest;
import com.oralie.products.sevice.ProductImageService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
public class ProductImageController {

    private final ProductImageService productImageService;

    @PostMapping("/dash/products/images")
    public ResponseEntity<Map> uploadFile(@ModelAttribute("file") MultipartFile file) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(productImageService.uploadFileSingle(file));
    }



}
