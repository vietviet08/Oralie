package com.oralie.products.model;

import com.oralie.products.dto.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

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
    @Column(name = "product_id")
    private Long id;

    @Column(unique = true)
    private String name;

    private String slug;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ProductCategory> productCategories;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductImage> images;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductOption> options;

    @Column(unique = true)
    private String sku;

    @Column(length = 1000)
    private String description;

    private Double price;

    private Boolean isDiscounted;

    private Double discount;

    private Integer quantity;

    private String image;

    private Boolean isAvailable;

    private Boolean isDeleted;

    private Boolean isFeatured;

    private Boolean isPromoted;

}
