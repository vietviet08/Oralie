package com.oralie.products.repository.client;

import com.oralie.products.model.s3.FileMetadata;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@FeignClient(name = "social", fallback = S3CallBack.class)
public interface S3FeignClient {

    @PostMapping("/store/upload")
    ResponseEntity<List<FileMetadata>> createAttachments(@RequestPart(value = "files") List<MultipartFile> files);


    @GetMapping("/store/view/{fileName}")
    ResponseEntity<InputStreamResource> viewFile(@PathVariable String fileName);

    @PostMapping("/store/download/{fileName}")
    ResponseEntity<InputStreamResource> downloadFile(@PathVariable String fileName);


    @DeleteMapping("/store/delete/{fileName}")
    ResponseEntity<String> deleteFile(@PathVariable String fileName);
}
