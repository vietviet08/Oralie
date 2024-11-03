package com.oralie.search.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductParam {
    private String keyword;
    private Integer pageNum;
    private Integer pageSize;
    private String sort;
    private String sortType;
    private String brand;
    private String category;
    private String option;
    private Double priceFrom;
    private Double priceTo;
}
