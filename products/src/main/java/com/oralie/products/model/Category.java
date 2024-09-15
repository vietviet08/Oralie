package com.oralie.products.model;

import com.oralie.products.dto.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;


@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "categories")
public class Category extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private String image;

    private Boolean isDeleted;

    private Boolean isFeatured;

    private Boolean isPromoted;

    private Boolean isDiscounted;

    private Double discount;
}
