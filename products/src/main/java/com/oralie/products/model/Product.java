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
@Table(name = "products")
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    private String description;

    private Double price;

    private Integer quantity;

    private String category;

    private String brand;

    private String color;

    private String size;

    private String image;

    private Boolean isAvailable;

    private Boolean isDeleted;

    private Boolean isFeatured;

    private Boolean isPromoted;

    private Boolean isDiscounted;

    private Double discount;

}
