package com.oralie.orders.model;

import com.oralie.orders.dto.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
@Builder
public class OrderAddress extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_address_id")
    private Long id;

    @Column(nullable = false)
    private String phoneNumber;
    private String email;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String addressDetail;

}
