package com.oralie.rates.dto.response;

import com.oralie.rates.model.Rate;
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

    private String userId;

    private Long productId;

    private String content;

    private List<String> urlFile;

    private List<UserRateComment> listUserLike; 

    private Long totalLike;

    private Long totalDislike;

    private Boolean isAvailable;

    private Long parentRate;

    private List<RateResponse> subRates;
}
