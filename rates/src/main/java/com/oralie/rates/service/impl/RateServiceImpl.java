package com.oralie.rates.service.impl;

import com.oralie.rates.dto.request.RateRequest;
import com.oralie.rates.dto.response.ListResponse;
import com.oralie.rates.dto.response.RateResponse;
import com.oralie.rates.model.Rate;
import com.oralie.rates.repository.RateRepository;
import com.oralie.rates.service.RateService;
import com.oralie.rates.service.SocialService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RateServiceImpl implements RateService {

	private final RateRepository rateRepository;
 	
	private final SocialService socialService;

	@Override
	public ListResponse<RateResponse> getAllRate(int page, int size, String sortBy, String sort) {
		Sort sortObj = sort.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
				: Sort.by(sortBy).descending();
		Pageable pageable = PageRequest.of(page, size, sortObj);
		Page<Rate> pageRates = rateRepository.findAllRates(pageable);
		List<Rate> rates = pageRates.getContent();

		return ListResponse
				.<RateResponse>builder()
				.data(mapToRateResponseList(rates))
				.pageNo(pageRates.getNumber())
				.pageSize(pageRates.getSize())
				.totalElements((int) pageRates.getTotalElements())
				.totalPages(pageRates.getTotalPages())
				.isLast(pageRates.isLast())
				.build();
	}

	private List<RateResponse> mapToRateResponseList(List<Rate> rates) {
		return null;
	}

	@Override
	public ListResponse<RateResponse> getAllRateByProductId(Long productId, int page, int size, String sortBy, String sort) {
		return null;
	}

	@Override
	public RateResponse postComment(Long productId, String userId, RateRequest rateRequest) {
		return null;
	}

	@Override
	public RateResponse updateComment(Long productId, String userId, RateRequest rateRequest) {
		return null;
	}

	@Override
	public void deleteComment(Long productId, String userId) {

	}
}
