package com.oralie.products;

import com.cloudinary.Cloudinary;
import com.cloudinary.api.ApiResponse;
import com.cloudinary.utils.ObjectUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ProductsApplicationTests {

    @Autowired
    private Cloudinary cloudinary;

    @Test
    void contextLoads() throws Exception {
        ApiResponse apiResponse = cloudinary.api().resourceByAssetID("a2d34885cceb8c0bba0dcf41b0544bf7", ObjectUtils.emptyMap());
        System.out.println(apiResponse);
    }

}
