package com.oralie.products.model;

import com.oralie.products.dto.entity.BaseEntity;
import com.oralie.products.dto.entity.listener.ProductListener;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
@Schema(name = "Product", description = "Schema define the parameters of product")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "products")
@EntityListeners(ProductListener.class)
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

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductSpecification> specifications;

    @Column(unique = true)
    private String sku;

    @Column(length = 1000)
    private String description;

    private Double price;

    private Double discount;

    private Long quantity;

    private String image;

    private Boolean isDiscounted;

    private Boolean isAvailable = true;

    private Boolean isDeleted = false;

    private Boolean isFeatured = true;

    private Boolean isPromoted = true;

}
