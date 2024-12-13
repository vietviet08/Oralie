package com.oralie.products.model;

import com.oralie.products.dto.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Schema(name = "Brand", description = "Schema define the parameters of brand")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "brands")
public class Brand extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "brand_id")
    private Long id;

    @OneToMany(mappedBy = "brand", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private List<Product> products;

    @Column(unique = true)
    private String name;

    @Column(unique = true)
    private String slug;

    @Column(length = 1000)
    private String description;

    private String image;

    private Boolean isActive = true;
}
