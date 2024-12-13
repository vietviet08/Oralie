package com.oralie.social;


import com.oralie.social.controller.S3Controller;
import com.oralie.social.dto.s3.FileMetadata;
import com.oralie.social.service.S3Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.amazonaws.services.s3.AmazonS3;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;


@WebMvcTest(S3Controller.class)
@ContextConfiguration(classes = {S3ControllerTest.TestConfig.class})
class S3ControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private S3Service s3Service;

    @MockBean
    private AmazonS3 amazonS3;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUploadImage() throws Exception {
        MockMultipartFile file = new MockMultipartFile("image", "test.jpg", MediaType.IMAGE_JPEG_VALUE, "test image content".getBytes());
        FileMetadata fileMetadata = new FileMetadata();
        fileMetadata.setName("test.jpg");

        when(s3Service.uploadImage(any())).thenReturn(fileMetadata);

        mockMvc.perform(multipart("/store/social/upload-image")
                        .file(file))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("test.jpg"));
    }

    @Test
    void testUploadImages() throws Exception {
        MockMultipartFile file = new MockMultipartFile("images", "test.jpg", MediaType.IMAGE_JPEG_VALUE, "test image content".getBytes());
        FileMetadata fileMetadata = new FileMetadata();
        fileMetadata.setName("test.jpg");

        when(s3Service.uploadImages(any())).thenReturn(Collections.singletonList(fileMetadata));

        mockMvc.perform(multipart("/store/social/upload-images")
                        .file(file))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$[0].name").value("test.jpg"));
    }

    @Test
    void testDeleteFile() throws Exception {
        mockMvc.perform(delete("/store/social/delete/test.jpg"))
                .andExpect(status().isNoContent());
    }

    @Configuration
    static class TestConfig {
        @Bean
        public AmazonS3 amazonS3() {
            return org.mockito.Mockito.mock(AmazonS3.class);
        }
    }
}