package com.oralie.rates.service;

import com.oralie.rates.dto.request.RateRequest;
import com.oralie.rates.dto.response.ListResponse;
import com.oralie.rates.dto.response.RateResponse;

public interface RateService {

    ListResponse<RateResponse> getAllRate(int page, int size, String sortBy, String sort);

    ListResponse<RateResponse> getAllRateByProductId(Long productId, int page, int size, String sortBy, String sort);

    ListResponse<RateResponse> getAllRateByUserId(String userId, int page, int size, String sortBy, String sort);

    RateResponse postComment(Long productId, String userId, RateRequest rateRequest);

    RateResponse updateComment(Long productId, String userId, RateRequest rateRequest);

    void deleteComment(Long rateId, String userId);

    void likeComment(Long rateId, Long productId, String userId);

    void dislikeComment(Long rateid, Long productId, String userId);

    Double avgRateStar(Long productId);

    void updateAvailableComment(Long rateId);
}
