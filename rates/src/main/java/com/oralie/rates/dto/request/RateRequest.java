package com.oralie.rates.dto.request;

import com.oralie.rates.model.Rate;
import jakarta.persistence.*;

import java.util.List;

public class RateRequest {
    private Long id;

    private String userId;

    private String content;

    private List<String> urlFile;

    private Boolean isAvailable;

    private Long parentRate;

    private List<Rate> subRates;

}
