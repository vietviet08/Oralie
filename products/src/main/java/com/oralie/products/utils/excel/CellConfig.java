package com.oralie.products.utils.excel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CellConfig {
    private int columnIndex;

    private String fieldName;
}