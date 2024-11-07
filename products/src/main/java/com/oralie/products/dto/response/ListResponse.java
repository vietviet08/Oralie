package com.oralie.products.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(name = "ListResponse", description = "Schema define the parameters to response the list of pages")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ListResponse <T>{
    private List<T> data;
    private int pageNo;
    private int pageSize;
    private int totalElements;
    private int totalPages;
    private boolean isLast;
}
