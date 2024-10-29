package com.oralie.social.controller;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.S3ClientOptions;
import com.oralie.social.dto.SocialContactDto;
import com.oralie.social.dto.s3.FileMetadata;
import com.oralie.social.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class S3Controller {

    private final Environment environment;

    @Value("${info.app.version}")
    private String build;

    private final SocialContactDto socialContactDto;

    private final S3Service s3Service;

    private final AmazonS3 s3client;

    @PostMapping("/store/upload")
    public ResponseEntity<List<FileMetadata>> createAttachments(@RequestPart(value = "files") List<MultipartFile> files) {
        s3client.listBuckets().forEach(bucket -> System.out.println(bucket.getName()));
        return new ResponseEntity<>(s3Service.uploadFile(files), HttpStatus.OK);
    }

    @GetMapping("/store/view/{fileName}")
    public ResponseEntity<InputStreamResource> viewFile(@PathVariable String fileName) {
        var s3Object = s3Service.getFile(fileName);
        var content = s3Object.getObjectContent();
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\""+fileName+"\"")
                .body(new InputStreamResource(content));
    }

    @PostMapping("/store/download/{fileName}")
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable String fileName) {
        var s3Object = s3Service.getFile(fileName);
        var content = s3Object.getObjectContent();
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""+fileName+"\"")
                .body(new InputStreamResource(content));
    }

    @DeleteMapping("/store/delete/{fileName}")
    public ResponseEntity<String> deleteFile(@PathVariable String fileName) {
        s3Service.deleteFile(fileName);
        return new ResponseEntity<>("Deleted", HttpStatus.NO_CONTENT);
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
