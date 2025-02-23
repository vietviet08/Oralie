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

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "user_info_id")
  private UserInfo userInfo;

  private Long productId;

  private Long orderItemId;

  private int rateStar;

  private String content;

  @ElementCollection
  private List<String> urlFile;

  @OneToMany(mappedBy = "rate", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private List<UserRateComment> listUserLike;

  private Long totalLike;

  private Long totalDislike;

  private Boolean isAvailable;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "parent_category_id")
  private Rate parentRate;

  @OneToMany(mappedBy = "parentRate", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private List<Rate> subRates;

}
