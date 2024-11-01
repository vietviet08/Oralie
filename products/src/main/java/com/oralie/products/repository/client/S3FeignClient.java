package com.oralie.products.repository.client;

import com.oralie.products.model.s3.FileMetadata;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@FeignClient(name = "social", fallback = S3FeignClientFallback.class)
public interface S3FeignClient {

    @PostMapping(value = "/store/social/upload-image"
            , consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}
    )
    public ResponseEntity<FileMetadata> uploadImage(@RequestPart(value = "image") MultipartFile image);

    @PostMapping("/store/social/upload-images")
    public ResponseEntity<List<FileMetadata>> uploadImages(@RequestPart(value = "images") List<MultipartFile> files);

    @PostMapping("/store/social/upload")
    public ResponseEntity<List<FileMetadata>> createAttachments(@RequestPart(value = "files") List<MultipartFile> files);

    @GetMapping("/store/social/view/{fileName}")
    public ResponseEntity<InputStreamResource> viewFile(@PathVariable String fileName);

    @PostMapping("/store/social/download/{fileName}")
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable String fileName);

    @DeleteMapping("/store/social/delete/{fileName}")
    public ResponseEntity<String> deleteFile(@PathVariable String fileName);
}
