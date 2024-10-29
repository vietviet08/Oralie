package com.oralie.products.controller;

import com.oralie.products.dto.request.ProductImageRequest;
import com.oralie.products.model.s3.FileMetadata;
import com.oralie.products.repository.client.S3FeignClient;
import com.oralie.products.sevice.ProductImageService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
public class ProductImageController {

    private final ProductImageService productImageService;

    private final S3FeignClient s3FeignClient;

    @PostMapping("/dash/products/images")
    public ResponseEntity<Map> uploadFile(@ModelAttribute("file") MultipartFile file) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(productImageService.uploadFileSingle(file));
    }

    @PostMapping("/store/products/upload-images")
    public ResponseEntity<List<FileMetadata>> uploadImage(@RequestParam("images") MultipartFile file) {
        return s3FeignClient.createAttachments(List.of(file));
    }


}
