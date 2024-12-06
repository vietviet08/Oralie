package com.oralie.rates.service.impl;

import com.oralie.rates.constant.RateConstant;
import com.oralie.rates.dto.FileMetadata;
import com.oralie.rates.dto.request.RateRequest;
import com.oralie.rates.dto.response.ListResponse;
import com.oralie.rates.dto.response.RateResponse;
import com.oralie.rates.dto.response.UserRateCommentResponse;
import com.oralie.rates.exception.ResourceNotFoundException;
import com.oralie.rates.model.Rate;
import com.oralie.rates.model.UserRateComment;
import com.oralie.rates.repository.RateRepository;
import com.oralie.rates.service.ProductService;
import com.oralie.rates.service.RateService;
import com.oralie.rates.service.SocialService;
import com.oralie.rates.service.AccountService;
import jakarta.ws.rs.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RateServiceImpl implements RateService {

    private static final Logger log = LoggerFactory.getLogger(RateServiceImpl.class);

    private final RateRepository rateRepository;

    private final SocialService socialService;
    private final AccountService accountService;
    private final ProductService productService;

    @Override
    public ListResponse<RateResponse> getAllRate(int page, int size, String sortBy, String sort) {
        Sort sortObj = sort.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sortObj);
        Page<Rate> pageRates = rateRepository.findAll(pageable);
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
    public ListResponse<RateResponse> getAllRateByUserId(String userId, int page, int size, String sortBy, String sort) {
        return null;
    }

    @Override
    public RateResponse postComment(Long productId, String userId, RateRequest rateRequest) {
        //check existing user
        boolean existUser = accountService.existingAccountByUserId(userId);

        if (!existUser) {
            log.error("Not existing account by UserId: {}", userId);
            throw new BadRequestException(RateConstant.NOT_EXISTING_USER);
        }

        List<String> urls = new ArrayList<>();

        if (rateRequest.getFiles() != null && !rateRequest.getFiles().isEmpty()) {
            List<FileMetadata> fileMetadatas = socialService.uploadImages(rateRequest.getFiles());
            fileMetadatas.forEach(fileMetadata -> urls.add(fileMetadata.getUrl()));
        }

        Rate parentRate = rateRepository.findById(rateRequest.getParentRate()).orElse(null);

        Rate rate = Rate.builder()
                .userId(userId)
                .productId(productId)
                .content(rateRequest.getContent())
                .urlFile(urls)
                .totalLike(0L)
                .totalDislike(0L)
                .listUserLike(null)
                .isAvailable(true)
                .parentRate(parentRate)
                .subRates(rateRequest.getSubRates())
                .build();
        return mapToRateResponse(rateRepository.save(rate));
    }

    @Override
    public RateResponse updateComment(Long productId, String userId, RateRequest rateRequest) {
        boolean existUser = accountService.existingAccountByUserId(userId);

        if (!existUser) {
            log.error("Not existing account by UserId: {}", userId);
            throw new BadRequestException(RateConstant.NOT_EXISTING_USER);
        }


        Rate rate = rateRepository.findById(rateRequest.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Rate not found", "id", rateRequest.getId().toString()));

        List<String> urls = new ArrayList<>();

        if (rateRequest.getFiles() != null && !rateRequest.getFiles().isEmpty()) {
            List<FileMetadata> fileMetadatas = socialService.uploadImages(rateRequest.getFiles());
            fileMetadatas.forEach(fileMetadata -> urls.add(fileMetadata.getUrl()));
        }

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
    public void deleteComment(Long rateId, String userId) {
        Rate rate = rateRepository.findById(rateId)
                .orElseThrow(() -> new ResourceNotFoundException("Rate not found", "rateId", rateId.toString()));
        rateRepository.delete(rate);
    }

    @Override
    public void likeComment(Long rateId, Long productId, String userId) {

        Rate rate = rateRepository.findById(rateId)
                .orElseThrow(() -> new ResourceNotFoundException("Rate not found", "rateId", rateId.toString()));

        boolean existUser = accountService.existingAccountByUserId(userId);

        if (!existUser) {
            log.error("Not existing account by UserId: {}", userId);
            throw new BadRequestException(RateConstant.NOT_EXISTING_USER);
        }

        long totalLike = rate.getTotalLike() != null ? rate.getTotalLike() : 0L;

        boolean isLike = rate.getListUserLike().stream()
                .anyMatch(userRateComment ->
                        userRateComment.getUserId().equalsIgnoreCase(userId) &&
                                Boolean.TRUE.equals(userRateComment.getIsLike()));

        if (isLike) {
            rate.setTotalLike(totalLike - 1);
            rate.getListUserLike().removeIf(userLiked -> userLiked.getUserId().equals(userId));
        } else {
            rate.setTotalLike(totalLike + 1);
            rate.getListUserLike().add(
                    UserRateComment.builder()
                            .userId(userId)
                            .rate(rate)
                            .productId(productId)
                            .isLike(true)
                            .build()
            );

        }

        rateRepository.save(rate);
    }

    @Override
    public void dislikeComment(Long rateId, Long productId, String userId) {

        Rate rate = rateRepository.findById(rateId)
                .orElseThrow(() -> new ResourceNotFoundException("Rate not found", "rateId", rateId.toString()));

        boolean existUser = accountService.existingAccountByUserId(userId);

        if (!existUser) {
            log.error("Not existing account by UserId: {}", userId);
            throw new BadRequestException(RateConstant.NOT_EXISTING_USER);
        }

        long totalDislike = rate.getTotalDislike() != null ? rate.getTotalDislike() : 0L;

        boolean isDislike = rate.getListUserLike().stream()
                .anyMatch(userRateComment ->
                        userRateComment.getUserId().equalsIgnoreCase(userId) &&
                                Boolean.FALSE.equals(userRateComment.getIsLike()));

        if (isDislike) {
            rate.setTotalDislike(totalDislike - 1);
            rate.getListUserLike().removeIf(userLiked -> userLiked.getUserId().equals(userId));
        } else {
            rate.setTotalDislike(totalDislike + 1);
            rate.getListUserLike().add(
                    UserRateComment.builder()
                            .userId(userId)
                            .rate(rate)
                            .productId(productId)
                            .isLike(false)
                            .build()
            );
        }

        rateRepository.save(rate);
    }

    @Override
    public Double avgRateStar(Long productId) {
        boolean existingProduct = productService.existingProductByProductId(productId);

        if (!existingProduct) {
            log.error("Not found Product with productId: {}", productId.toString());
            throw new ResourceNotFoundException("Product", "productId", productId.toString());
        }

        List<Rate> rates = rateRepository.findByProductId(productId);
        if (rates.isEmpty()) return 0.0;
        return rates.stream()
                .mapToInt(Rate::getRateStar)
                .average()
                .orElse(0.0);
    }

    @Override
    public void updateAvailableComment(Long rateId) {
        Rate rate = rateRepository.findById(rateId)
                .orElseThrow(() -> new ResourceNotFoundException("Rate", "rateId", rateId.toString()));

        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        if (!userId.equals(rate.getUserId()))
            throw new BadRequestException(RateConstant.RATE_NOT_MATCH_USER);

        log.info("User with id: {}, hiding comment", userId);

        rate.setIsAvailable(!rate.getIsAvailable());
        rateRepository.save(rate);
    }

    private RateResponse mapToRateResponse(Rate rate) {
        return RateResponse.builder()
                .id(rate.getId())
                .userId(rate.getUserId())
                .productId(rate.getProductId())
                .rateStar(rate.getRateStar())
                .content(rate.getContent())
                .urlFile(rate.getUrlFile())
                .totalLike(rate.getTotalLike())
                .totalDislike(rate.getTotalDislike())
                .listUserLike(rate.getListUserLike() != null ? mapToUserRateCommentResponse(rate.getListUserLike()) : null)
                .isAvailable(rate.getIsAvailable())
                .parentRate(rate.getParentRate() != null ? rate.getParentRate().getId() : null)
                .subRates(mapToRateResponseList(rate.getSubRates()))
                .build();
    }

    private List<RateResponse> mapToRateResponseList(List<Rate> rates) {
        List<RateResponse> rateResponses = new ArrayList<>();
        rates.forEach(rate -> {
            RateResponse rateResponse = RateResponse.builder()
                    .id(rate.getId())
                    .userId(rate.getUserId())
                    .productId(rate.getProductId())
                    .rateStar(rate.getRateStar())
                    .content(rate.getContent())
                    .urlFile(rate.getUrlFile())
                    .totalLike(rate.getTotalLike())
                    .listUserLike(rate.getListUserLike() != null ? mapToUserRateCommentResponse(rate.getListUserLike()) : null)
                    .isAvailable(rate.getIsAvailable())
                    .parentRate(rate.getParentRate() != null ? rate.getParentRate().getId() : null)
                    .subRates(mapToRateResponseList(rate.getSubRates()))
                    .build();
            rateResponses.add(rateResponse);
        });
        return rateResponses;
    }

    private List<UserRateCommentResponse> mapToUserRateCommentResponse(List<UserRateComment> userRateComments  ){
        List<UserRateCommentResponse> userRateCommentResponses = new ArrayList<>();
        userRateComments.forEach(userRateComment -> {
            UserRateCommentResponse userRateCommentResponse = UserRateCommentResponse.builder()
                    .id(userRateComment.getId())
                    .rateId(userRateComment.getRate().getId())
                    .userId(userRateComment.getUserId())
                    .productId(userRateComment.getProductId())
                    .isLike(userRateComment.getIsLike())
                    .build();
            userRateCommentResponses.add(userRateCommentResponse);
        });
        return userRateCommentResponses;
    }
}
