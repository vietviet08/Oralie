package com.oralie.search.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(indexName = "product")
@Setting(settingPath = "esconfig/elastic-analyzer.json")
public class ProductDocument {

    @Id
    private Long id;

    @Field(type = FieldType.Text, analyzer = "autocomplete_index", searchAnalyzer = "autocomplete_search")
    private String productName;

    @Field(type = FieldType.Text)
    private String slug;

    @Field(type = FieldType.Keyword)
    private List<String> categories;

    @Field(type = FieldType.Keyword)
    private List<String> options;

    @Field(type = FieldType.Text)
    private String brand;

    private String sku;

    @Field(type = FieldType.Text)
    private String description;

    @Field(name = "product_price", type = FieldType.Double)
    private Double price;

    private Double discount;

    private Long quantity;

    private Boolean isDiscounted;

    private Boolean isAvailable;

    private Boolean isDeleted;

    private Boolean isFeatured;

    private Boolean isPromoted;
}
