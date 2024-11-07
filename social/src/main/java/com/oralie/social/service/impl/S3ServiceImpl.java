package com.oralie.social.service.impl;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.oralie.social.constant.SocialConstant;
import com.oralie.social.dto.s3.FileMetadata;
import com.oralie.social.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service {

    private static final Logger log = LoggerFactory.getLogger(S3ServiceImpl.class);

    @Value("${aws.bucket.name}")
    private String BUCKET_NAME;

    private final AmazonS3 amazonS3;

    private final Tika tika = new Tika();

    @Override
    public Bucket createBucket(String bucketName) {
        if (amazonS3.doesBucketExistV2(bucketName)) {
            return amazonS3.createBucket(bucketName);
        }
        return amazonS3.createBucket(bucketName);
    }

    @Override
    public FileMetadata uploadImage(MultipartFile image) {
        if (image == null) {
            return null;
        }

        String fileKey = "oralie-file-" + image.getOriginalFilename();
        if (Objects.requireNonNull(image.getOriginalFilename()).contains(fileKey)) {
            fileKey = image.getOriginalFilename();
        }

        return put(BUCKET_NAME, fileKey, image, true);
    }

    @Override
    public List<FileMetadata> uploadImages(List<MultipartFile> files) {
        List<FileMetadata> fileMetadata = new ArrayList<>();

        for (MultipartFile file : files) {
            String fileKey = "oralie-file-" + file.getOriginalFilename();
            if (Objects.requireNonNull(file.getOriginalFilename()).contains(fileKey)) {
                fileKey = file.getOriginalFilename();
            }
            fileMetadata.add(put(BUCKET_NAME, fileKey, file, true));
        }

        return fileMetadata;
    }

    @Override
    public S3Object getFile(String keyName) {
        return amazonS3.getObject(BUCKET_NAME, keyName);
    }

    @Override
    public void downloadFile(String keyName, String downloadFilePath) {

    }

    @Override
    public void deleteFile(String keyName) {
        try {
            log.info("Deleting file from S3: {}", keyName);

            if (keyName == null) {
                return;
            } else if (keyName.contains(SocialConstant.URL_STORAGE)) {
                keyName = keyName.replace(SocialConstant.URL_STORAGE, "");
            }

            log.info("Deleting file from S3: {}", keyName);

            DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(BUCKET_NAME, keyName);
            amazonS3.deleteObject(deleteObjectRequest);
        } catch (AmazonServiceException e) {

            log.error("Error deleting file from S3", e);

        }

        log.info("File deleted from S3: {}", keyName);
    }

    @Override
    public void deleteFiles(List<String> keyNames) {
        for (String keyName : keyNames) {
            deleteFile(keyName);
        }
    }

    public FileMetadata put(String bucket, String key, MultipartFile file, Boolean publicAccess) {
        FileMetadata metadata = FileMetadata.builder()
                .bucket(bucket)
                .key(key)
                .name(file.getOriginalFilename())
                .extension(StringUtils.getFilenameExtension(file.getOriginalFilename()))
                .mime(tika.detect(file.getOriginalFilename()))
                .size(file.getSize())
                .build();

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(metadata.getSize());
        objectMetadata.setContentType(metadata.getMime());

        log.info("Uploading file to S3: {}", metadata.getName());

        try {
            InputStream stream = file.getInputStream();
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, key, stream, objectMetadata);
            PutObjectResult putObjectResult = amazonS3.putObject(putObjectRequest);
            metadata.setUrl(amazonS3.getUrl(bucket, key).toString());
            metadata.setHash(putObjectResult.getContentMd5());
            metadata.setEtag(putObjectResult.getETag());
            metadata.setPublicAccess(publicAccess);
        } catch (IOException e) {
            log.error("Error uploading file to S3", e);
        }
        return metadata;
    }
}
