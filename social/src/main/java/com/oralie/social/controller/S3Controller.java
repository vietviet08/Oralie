package com.oralie.social.controller;

import com.amazonaws.services.s3.AmazonS3;
import com.oralie.social.dto.SocialContactDto;
import com.oralie.social.dto.s3.FileMetadata;
import com.oralie.social.service.S3Service;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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

    @PostMapping(value = "/dash/social/upload-image")
    public ResponseEntity<FileMetadata> uploadImage(@RequestPart(value = "image") MultipartFile image) {
        s3client.listBuckets().forEach(bucket -> System.out.println(bucket.getName()));
        return new ResponseEntity<>(s3Service.uploadImage(image), HttpStatus.OK);
    }

    @PostMapping(value = "/dash/social/upload-images")
    public ResponseEntity<List<FileMetadata>> uploadImages(@RequestPart(value = "images") List<MultipartFile> files) {
        s3client.listBuckets().forEach(bucket -> System.out.println(bucket.getName()));
        return new ResponseEntity<>(s3Service.uploadImages(files), HttpStatus.OK);
    }

    @PostMapping(value = "/dash/social/upload-image-url")
    public ResponseEntity<FileMetadata> uploadImageByUrl(@RequestBody String url) {
        s3client.listBuckets().forEach(bucket -> System.out.println(bucket.getName()));
        return new ResponseEntity<>(s3Service.uploadImageByUrl(url), HttpStatus.OK);
    }

    @PostMapping(value = "/dash/social/upload-images-url")
    public ResponseEntity<List<FileMetadata>> uploadImagesByUrl(@RequestBody List<String> urls) {
        s3client.listBuckets().forEach(bucket -> System.out.println(bucket.getName()));
        return new ResponseEntity<>(s3Service.uploadImagesByUrl(urls), HttpStatus.OK);
    }

    @PostMapping(value = "/dash/social/upload")
    public ResponseEntity<List<FileMetadata>> createAttachments(@RequestPart(value = "files") List<MultipartFile> files) {
        s3client.listBuckets().forEach(bucket -> System.out.println(bucket.getName()));
        return new ResponseEntity<>(s3Service.uploadImages(files), HttpStatus.OK);
    }

    @GetMapping("/store/social/view/{fileName}")
    public ResponseEntity<InputStreamResource> viewFile(@PathVariable String fileName) {
        var s3Object = s3Service.getFile(fileName);
        var content = s3Object.getObjectContent();
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                .body(new InputStreamResource(content));
    }

    @PostMapping("/store/social/download/{fileName}")
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable String fileName) {
        var s3Object = s3Service.getFile(fileName);
        var content = s3Object.getObjectContent();
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(new InputStreamResource(content));
    }

    @DeleteMapping(value = "/dash/social/delete/{fileName}")
    public ResponseEntity<String> deleteFile(@PathVariable String fileName) {
        s3Service.deleteFile(fileName);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body("File deleted successfully");
    }

    @DeleteMapping(value = "/dash/social/delete")
    public ResponseEntity<String> deleteFiles(@RequestPart List<String> fileName) {
        s3Service.deleteFiles(fileName);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body("File deleted successfully");
    }

    //info
    @GetMapping("/social/build-version")
    public ResponseEntity<String> getBuildVersion() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(build);
    }

    @GetMapping("/social/java-version")
    public ResponseEntity<String> getJavaVersion() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(environment.getProperty("JAVA_HOME"));
    }

    @GetMapping("/social/contact-info")
    public ResponseEntity<SocialContactDto> getProductsContactDto() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(socialContactDto);
    }

}
