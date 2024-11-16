package com.oralie.products.service.impl;

import com.cloudinary.Cloudinary;
import com.oralie.products.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryServiceImpl implements CloudinaryService {

    private final Cloudinary cloudinary;
    private static final String FOLDER_NAME = "products";
    private static final Logger log = LoggerFactory.getLogger(CloudinaryServiceImpl.class);

    @Override
    public List<String> uploadFile(List<MultipartFile> files, String folderName) {
        List<String> urls = new ArrayList<>();

        for (MultipartFile file : files) {
            try {
                Map<String, String> options = new HashMap<>();
                options.put("folder", folderName);
                options.put("resource_type", "auto");
                options.put("public_id", file.getOriginalFilename());
                Map uploadResult = cloudinary.uploader().upload(file.getBytes(), options);
                urls.add((String) uploadResult.get("url"));
            } catch (IOException e) {
                log.error("Error uploading file: {}", e.getMessage());
            }
        }

        return urls;
    }

    @Override
    public String uploadFileSingle(MultipartFile file, String folderName) throws IOException {
        try{

            HashMap<Object, Object> options = new HashMap<>();
            options.put("folder", folderName);
            Map uploadedFile = cloudinary.uploader().upload(file.getBytes(), options);
            String publicId = (String) uploadedFile.get("public_id");

            return cloudinary.url().secure(true).generate(publicId);

        }catch (IOException e){
            log.error("Error uploading file: {}", e.getMessage());
            return null;

        }
    }
}
