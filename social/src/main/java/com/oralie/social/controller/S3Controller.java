package com.oralie.social.controller;

import com.amazonaws.services.s3.AmazonS3;
import com.oralie.social.dto.SocialContactDto;
import com.oralie.social.dto.s3.FileMetadata;
import com.oralie.social.service.S3Service;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Controller for handling operations with S3 such as uploading, downloading, and deleting files.
 */
@Slf4j
@Tag(
        name = "The API of S3 Service",
        description = "This API allows you to upload, download and delete files from S3 bucket"
)
@RestController
@RequiredArgsConstructor
@RequestMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
public class S3Controller {

    private final Environment environment;

    @Value("${info.app.version}")
    private String build;

    private final SocialContactDto socialContactDto;

    private final S3Service s3Service;

    private final AmazonS3 s3client;

    @GetMapping("/store/social/view/{fileName}")
    public ResponseEntity<InputStreamResource> viewFile(@PathVariable String fileName) {
        try {
            var s3Object = s3Service.getFile(fileName);
            var content = s3Object.getObjectContent();
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(s3Object.getObjectMetadata().getContentType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                    .body(new InputStreamResource(content));
        } catch (Exception e) {
            log.error("File viewing failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping(value = "/dash/social/upload-image")
    public ResponseEntity<FileMetadata> uploadImage(@RequestPart(value = "image") MultipartFile image) {
        logBucketNames();
        return new ResponseEntity<>(s3Service.uploadImage(image), HttpStatus.CREATED);
    }

    @PostMapping(value = "/dash/social/upload-images")
    public ResponseEntity<List<FileMetadata>> uploadImages(@RequestPart(value = "images") List<MultipartFile> files) {
        logBucketNames();
        return new ResponseEntity<>(s3Service.uploadImages(files), HttpStatus.CREATED);
    }

    @PostMapping(value = "/dash/social/upload-image-url")
    public ResponseEntity<FileMetadata> uploadImageByUrl(@RequestBody String url) {
        logBucketNames();
        return new ResponseEntity<>(s3Service.uploadImageByUrl(url), HttpStatus.CREATED);
    }

    @PostMapping(value = "/dash/social/upload-images-url")
    public ResponseEntity<List<FileMetadata>> uploadImagesByUrl(@RequestBody List<String> urls) {
        logBucketNames();
        return new ResponseEntity<>(s3Service.uploadImagesByUrl(urls), HttpStatus.CREATED);
    }

    @PostMapping(value = "/dash/social/upload")
    public ResponseEntity<List<FileMetadata>> createAttachments(@RequestPart(value = "files") List<MultipartFile> files) {
        logBucketNames();
        return new ResponseEntity<>(s3Service.uploadImages(files), HttpStatus.CREATED);
    }

    @PostMapping("/dash/social/download/{fileName}")
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable String fileName) {
        try {
            var s3Object = s3Service.getFile(fileName);
            var content = s3Object.getObjectContent();
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .body(new InputStreamResource(content));
        } catch (Exception e) {
            log.error("File download failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping(value = "/dash/social/delete/{fileName}")
    public ResponseEntity<String> deleteFile(@PathVariable String fileName) {
        try {
            s3Service.deleteFile(fileName);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("File deleted successfully");
        } catch (Exception e) {
            log.error("File deletion failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting file");
        }
    }

    @DeleteMapping(value = "/dash/social/delete")
    public ResponseEntity<String> deleteFiles(@RequestBody List<String> fileName) {
        try {
            s3Service.deleteFiles(fileName);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Files deleted successfully");
        } catch (Exception e) {
            log.error("Files deletion failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting files");
        }
    }

    @GetMapping("/social/build-version")
    public ResponseEntity<String> getBuildVersion() {
        return ResponseEntity.status(HttpStatus.OK).body(build);
    }

    @GetMapping("/social/java-version")
    public ResponseEntity<String> getJavaVersion() {
        return ResponseEntity.status(HttpStatus.OK).body(environment.getProperty("JAVA_HOME"));
    }

    @GetMapping("/social/contact-info")
    public ResponseEntity<SocialContactDto> getProductsContactDto() {
        return ResponseEntity.status(HttpStatus.OK).body(socialContactDto);
    }

    private void logBucketNames() {
        s3client.listBuckets().forEach(bucket -> log.info("Bucket name: {}", bucket.getName()));
    }
}