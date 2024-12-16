package com.oralie.search.controller;

import com.oralie.search.dto.ProductParam;
import com.oralie.search.dto.response.ListResponse;
import com.oralie.search.model.ProductDocument;
import com.oralie.search.service.ProductSearchService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@Tag(
        name = "The API of Search Service",
        description = "This API provides operations for Search Service"
)
@RestController
@RequiredArgsConstructor
@RequestMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
public class SearchController {

    private final ProductSearchService productSearchService;

    @GetMapping("/store/search/search-product")
    public ResponseEntity<ListResponse<ProductDocument>> searchProduct(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
            @RequestParam(value = "sort", required = false, defaultValue = "ASC") String sort,
            @RequestParam(value = "sortBy", required = false, defaultValue = "id") String sortBy,
            @RequestParam(value = "category", required = false, defaultValue = "") String category,
            @RequestParam(value = "brand", required = false, defaultValue = "") String brand,
            @RequestParam(value = "option", required = false, defaultValue = "") String option,
            @RequestParam(value = "price", required = false, defaultValue = "") String price) {

        ProductParam productParam = ProductParam.builder()
                .keyword(keyword)
                .pageNum(page)
                .pageSize(size)
                .sort(sort)
                .sortType(sortBy)
                .category(category)
                .brand(brand)
                .option(option)
                .priceFrom(!price.isEmpty() ? Double.parseDouble(price.split("-")[0]) : null)
                .priceTo(!price.isEmpty() ? Double.parseDouble(price.split("-")[1]) : null)
                .build();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(productSearchService.searchProducts(productParam));
    }

    @GetMapping("/store/search/search-autocomplete")
    public ResponseEntity<List<String>> searchAutoComplete(@RequestParam(value = "keyword") String keyword) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(productSearchService.autoCompleteProductName(keyword));
    }

}
