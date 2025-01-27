package com.oralie.accounts.model;

import com.oralie.accounts.dto.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_attribute")
@Builder
public class UserAttribute extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ElementCollection
    private List<String> phone;

    @ElementCollection
    private List<String> addressDetail;

    @ElementCollection
    private List<String> city;

    @ElementCollection
    private List<String> picture;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private Account account;
}
