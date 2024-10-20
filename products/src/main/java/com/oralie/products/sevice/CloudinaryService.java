package com.oralie.products.sevice;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface CloudinaryService {
    List<String> uploadFile(List<MultipartFile> file, String folderName);

    String uploadFileSingle(MultipartFile file, String folderName) throws IOException;
}
