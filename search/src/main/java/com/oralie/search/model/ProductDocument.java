package com.oralie.search.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(indexName = "product")
public class ProductDocument {

    @Id
    private Long id;

    @Field(name = "product_name")
    private String productName;

    private Integer quantity;

}
