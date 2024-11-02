package com.oralie.social.service;

import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.S3Object;
import com.oralie.social.dto.s3.FileMetadata;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface S3Service {
    Bucket createBucket(String bucketName);

    FileMetadata uploadImage(MultipartFile image);

    List<FileMetadata> uploadImages(List<MultipartFile> files);

    S3Object getFile(String keyName);

    void downloadFile(String keyName, String downloadFilePath);

    void deleteFile(String keyName);

    void deleteFiles(List<String> keyNames);

}
