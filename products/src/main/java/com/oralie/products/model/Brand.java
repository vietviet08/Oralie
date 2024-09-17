package com.oralie.products.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "brands")
public class Brand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "brand_id")
    private Long id;

    @OneToMany(mappedBy = "brand", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Product> products;

    private String name;

    @Column(length = 1000)
    private String description;

    private String imageUrl;

    private Boolean isActive;
}
