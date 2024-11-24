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

    void deleteComment(Long productId, String userId);

    void likeComment(Long rateId, String userId);

    void disLikeComment(Long rateid, String userId);

    double avgRateStar(Long productId);
}
