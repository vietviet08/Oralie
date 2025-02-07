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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

        return putByMultipartFile(BUCKET_NAME, fileKey, image, true);
    }

    @Override
    public List<FileMetadata> uploadImages(List<MultipartFile> files) {
        List<FileMetadata> fileMetadata = new ArrayList<>();

        for (MultipartFile file : files) {
            String fileKey = "oralie-file-" + file.getOriginalFilename();
            if (Objects.requireNonNull(file.getOriginalFilename()).contains(fileKey)) {
                fileKey = file.getOriginalFilename();
            }
            fileMetadata.add(putByMultipartFile(BUCKET_NAME, fileKey, file, true));
        }

        return fileMetadata;
    }

    @Override
    public FileMetadata uploadImageByUrl(String imageUrl) {
        assert imageUrl != null;
        String fileKey = Paths.get(imageUrl).getFileName().toString();
        return putByUrl(BUCKET_NAME, fileKey, imageUrl, true);
    }

    @Override
    public List<FileMetadata> uploadImagesByUrl(List<String> imageUrl) {
        List<FileMetadata> fileMetadata = new ArrayList<>();

        for (String url : imageUrl) {
            try {
                URL urlObj = new URL(url);
                String fileKey = new File(urlObj.getPath()).getName();

                fileMetadata.add(putByUrl(BUCKET_NAME, fileKey, url, true));
            } catch (MalformedURLException e) {
                log.error("Invalid URL format: {}", url, e);
            }
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

    public FileMetadata putByMultipartFile(String bucket, String key, MultipartFile file, Boolean publicAccess) {
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
            stream.close();
        } catch (IOException e) {
            log.error("Error uploading file to S3", e);
        }
        return metadata;
    }

    public FileMetadata putByUrl(String bucket, String key, String url, Boolean publicAccess) {
        try {
            URL urlImage = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) urlImage.openConnection();

            try (InputStream inputStream = connection.getInputStream()) {
                byte[] bytes = inputStream.readAllBytes();
                long contentLength = bytes.length;

                if (contentLength <= 0) {
                    throw new IOException("Failed to retrieve content or invalid content length for URL: " + url);
                }

                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentLength(contentLength);
                metadata.setContentType(tika.detect(new ByteArrayInputStream(bytes)));

                try (InputStream byteArrayStream = new ByteArrayInputStream(bytes)) {
                    PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, key, byteArrayStream, metadata);
                    PutObjectResult putObjectResult = amazonS3.putObject(putObjectRequest);

                    return FileMetadata.builder()
                            .bucket(bucket)
                            .key(key)
                            .name(new File(urlImage.getPath()).getName())
                            .extension(StringUtils.getFilenameExtension(key))
                            .mime(metadata.getContentType())
                            .size(metadata.getContentLength())
                            .url(amazonS3.getUrl(bucket, key).toString())
                            .hash(putObjectResult.getContentMd5())
                            .etag(putObjectResult.getETag())
                            .publicAccess(publicAccess)
                            .build();
                }
            }
        } catch (IOException e) {
            log.error("Error uploading image from URL to S3", e);
            return null;
        }
    }


}
