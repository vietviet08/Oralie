package com.oralie.rates.dto.response;

import com.oralie.rates.model.Rate;

import java.util.List;

public class RateResponse {
    private Long id;

    private String userId;

    private String content;

    private List<String> urlFile;

    private Boolean isAvailable;

    private Long parentRate;

    private List<Rate> subRates;
}
