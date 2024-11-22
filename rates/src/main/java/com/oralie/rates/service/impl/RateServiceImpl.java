package com.oralie.rates.service.impl;

import com.oralie.rates.dto.FileMetadata;
import com.oralie.rates.dto.request.RateRequest;
import com.oralie.rates.dto.response.ListResponse;
import com.oralie.rates.dto.response.RateResponse;
import com.oralie.rates.exception.ResourceNotFoundException;
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

import java.util.ArrayList;
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

	@Override
	public ListResponse<RateResponse> getAllRateByProductId(Long productId, int page, int size, String sortBy, String sort) {
		Sort sortObj = sort.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
				: Sort.by(sortBy).descending();
		Pageable pageable = PageRequest.of(page, size, sortObj);
		Page<Rate> pageRates = rateRepository.findAllByProductId(productId, pageable);
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

	@Override
	public RateResponse postComment(Long productId, String userId, RateRequest rateRequest) {
		List<FileMetadata> fileMetadatas = socialService.uploadImages(rateRequest.getFiles());
		List<String> urls = new ArrayList<>();
		fileMetadatas.forEach(fileMetadata -> urls.add(fileMetadata.getUrl()));

		Rate parentRate = rateRepository.findById(rateRequest.getParentRate()).orElse(null);

		Rate rate = Rate.builder()
				.userId(userId)
				.productId(productId)
				.content(rateRequest.getContent())
				.urlFile(urls)
				.isAvailable(true)
				.parentRate(parentRate)
				.subRates(rateRequest.getSubRates())
				.build();
		return mapToRateResponse(rateRepository.save(rate));
	}

	@Override
	public RateResponse updateComment(Long productId, String userId, RateRequest rateRequest) {
		Rate rate = rateRepository.findById(rateRequest.getId())
				.orElseThrow(() -> new ResourceNotFoundException("Rate not found", "id", rateRequest.getId().toString()));
		List<FileMetadata> fileMetadatas = socialService.uploadImages(rateRequest.getFiles());
		List<String> urls = new ArrayList<>();
		fileMetadatas.forEach(fileMetadata -> urls.add(fileMetadata.getUrl()));

		Rate parentRate = rateRepository.findById(rateRequest.getId()).orElse(null);

		rate.setUserId(userId);
		rate.setProductId(productId);
		rate.setContent(rateRequest.getContent());
		rate.setUrlFile(urls);
		rate.setIsAvailable(true);
		rate.setParentRate(parentRate);
		rate.setSubRates(rateRequest.getSubRates());
		return mapToRateResponse(rateRepository.save(rate));
	}

	@Override
	public void deleteComment(Long productId, String userId) {
		Rate rate = rateRepository.findByUserIdAndProductId(userId, productId)
				.orElseThrow(() -> new ResourceNotFoundException("Rate not found", "userId", userId));
		rateRepository.delete(rate);
	}

	private RateResponse mapToRateResponse(Rate rate) {
		return RateResponse.builder()
				.id(rate.getId())
				.userId(rate.getUserId())
				.productId(rate.getProductId())
				.content(rate.getContent())
				.urlFile(rate.getUrlFile())
				.isAvailable(rate.getIsAvailable())
				.parentRate(rate.getParentRate().getId())
				.subRates(rate.getSubRates())
				.build();
	}

	private List<RateResponse> mapToRateResponseList(List<Rate> rates) {
		List<RateResponse> rateResponses = new ArrayList<>();
		rates.forEach(rate -> {
			RateResponse rateResponse = RateResponse.builder()
					.id(rate.getId())
					.userId(rate.getUserId())
					.productId(rate.getProductId())
					.content(rate.getContent())
					.urlFile(rate.getUrlFile())
					.isAvailable(rate.getIsAvailable())
					.parentRate(rate.getParentRate().getId())
					.subRates(rate.getSubRates())
					.build();
			rateResponses.add(rateResponse);
		});
		return rateResponses;
	}
}
