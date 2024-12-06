package com.oralie.products;

import com.oralie.products.controller.BrandController;
import com.oralie.products.dto.request.BrandRequest;
import com.oralie.products.dto.response.BrandResponse;
import com.oralie.products.dto.response.ListResponse;
import com.oralie.products.service.BrandService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class BrandControllerTest {

    @Mock
    private BrandService brandService;

    @InjectMocks
    private BrandController brandController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllBrands() {
        // Arrange
        int page = 0;
        int size = 10;
        String sortBy = "id";
        String sort = "asc";
        ListResponse<BrandResponse> listResponse = new ListResponse<>();
        when(brandService.getAllBrands(page, size, sortBy, sort)).thenReturn(listResponse);

        // Act
        ResponseEntity<ListResponse<BrandResponse>> responseEntity = brandController.getAllBrands(page, size, sortBy, sort);

        // Assert
        assertEquals(ResponseEntity.ok(listResponse), responseEntity);
        verify(brandService, times(1)).getAllBrands(page, size, sortBy, sort);
    }

    @Test
    void getBrandById() {
        // Arrange
        Long brandId = 1L;
        BrandResponse brandResponse = new BrandResponse();
        when(brandService.getBrandById(brandId)).thenReturn(brandResponse);

        // Act
        ResponseEntity<BrandResponse> responseEntity = brandController.getBrandById(brandId);

        // Assert
        assertEquals(ResponseEntity.ok(brandResponse), responseEntity);
        verify(brandService, times(1)).getBrandById(brandId);
    }

    @Test
    public void createBrand() {
        // Assuming there's a method to create a BrandRequest object
        MultipartFile image = null; // Since the test might not be uploading an actual file.
        BrandRequest newBrand = new BrandRequest("BrandName", "name-slug","Description", image, true);

        ResponseEntity<BrandResponse> response = brandController.createBrand(newBrand);

        // Updated assertion to expect 201 CREATED status
        assertEquals(201, response.getStatusCodeValue());
        // You can add more assertions here to check the content of the response as needed
    }

    @Test
    void updateBrand() {
        // Arrange
        Long brandId = 1L;
        BrandResponse brandResponse = new BrandResponse();
        when(brandService.updateBrand(eq(brandId), any())).thenReturn(brandResponse);

        // Act
        ResponseEntity<BrandResponse> responseEntity = brandController.updateBrand(brandId, new BrandRequest());

        // Assert
        assertEquals(ResponseEntity.ok(brandResponse), responseEntity);
        verify(brandService, times(1)).updateBrand(eq(brandId), any());
    }

    @Test
    void deleteBrand() {
        // Arrange
        Long brandId = 1L;
        doNothing().when(brandService).deleteBrand(brandId);

        // Act
        ResponseEntity<Void> responseEntity = brandController.deleteBrand(brandId);

        // Assert
        assertEquals(ResponseEntity.noContent().build(), responseEntity);
        verify(brandService, times(1)).deleteBrand(brandId);
    }

}