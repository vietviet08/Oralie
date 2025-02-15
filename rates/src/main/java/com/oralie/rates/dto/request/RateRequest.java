package com.oralie.rates.dto.request;

import com.oralie.rates.model.Rate;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RateRequest {
    private Long id;

    private UserInfoRequest userInfo;

    private Long productId;

    private Long orderItemId;

    private int rateStar;

    private String content;

    private List<MultipartFile> files;

    private Boolean isAvailable;

    private Long parentRate;

    private List<Rate> subRates;

}
