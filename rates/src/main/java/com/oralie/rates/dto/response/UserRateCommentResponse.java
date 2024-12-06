package com.oralie.rates.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRateCommentResponse {
    private Long id;

    private Long rateId;

    private String userId;

    private Long productId;

    private Boolean isLike;

}
