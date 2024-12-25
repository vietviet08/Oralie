package com.oralie.rates.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RateResponse {
    private Long id;

    private UserInfoResponse userInfo;

    private Long productId;

    private Long orderItemId;

    private int rateStar;

    private String content;

    private List<String> urlFile;

    private String latestDateModified;

    private List<UserRateCommentResponse> listUserLike;

    private Long totalLike;

    private Long totalDislike;

    private Boolean isAvailable;

    private Long parentRate;

    private List<RateResponse> subRates;
}
