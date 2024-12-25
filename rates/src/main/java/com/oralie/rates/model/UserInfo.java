package com.oralie.rates.model;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "user_info")
public class UserInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_info_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String userId;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String urlAvatar;

}
