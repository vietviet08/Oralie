package com.oralie.rates.model;

import com.oralie.rates.dto.entity.BaseEntity;
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
@Table(name = "rate")
public class Rate extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;

    private String content;

    private List<String> urlFile;

    private Boolean isAvailable;
   
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_category_id")
    private Rate parentRate;

    @OneToMany(mappedBy = "parentRate", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Rate> subRates;

}
