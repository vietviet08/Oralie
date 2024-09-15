package com.oralie.products.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "brands")
public class Brand {

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
