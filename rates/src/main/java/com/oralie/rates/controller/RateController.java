package com.oralie.rates.controller;

import com.oralie.rates.dto.RateContactDto;
import com.oralie.rates.dto.request.RateRequest;
import com.oralie.rates.dto.response.ListResponse;
import com.oralie.rates.dto.response.RateResponse;
import com.oralie.rates.service.RateService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


@Tag(
        name = "The API of Rate Service",
        description = "This API provides operations for Rate Service"
)
@RestController
@RequiredArgsConstructor
@RequestMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
public class RateController {
    private final Environment environment;

    private final RateContactDto rateContactDto;

    @Value("${info.app.version}")
    private String build;

    private final RateService rateService;

    //dash

    @GetMapping(value = "/rates/dash/all")
    public ResponseEntity<ListResponse<RateResponse>> getAllRate(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sort) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(rateService.getAllRate(page, size, sortBy, sort));

    }

    //store

    @GetMapping(value = "/rates/store/{productId}")
    public ResponseEntity<ListResponse<RateResponse>> getAllRate(
            @PathVariable("productId") Long productId,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sort) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(rateService.getAllRateByProductId(productId, page, size, sortBy, sort));

    }

    @PostMapping(value = "/rates/store/{productId}")
    public ResponseEntity<RateResponse> postComment(
            @PathVariable Long productId,
            @RequestBody RateRequest rateRequest
    ) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.status(HttpStatus.OK)
                .body(rateService.postComment(productId, userId, rateRequest));
    }

    @PutMapping(value = "/rates/store/{productId}")
    public ResponseEntity<RateResponse> updateComment(
            @PathVariable Long productId,
            @RequestBody RateRequest rateRequest) {

        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.status(HttpStatus.OK)
                .body(rateService.updateComment(productId, userId, rateRequest));
    }

    @DeleteMapping(value = "/rates/store/{rateId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long rateId) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        rateService.deleteComment(rateId, userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }

    @PutMapping(value = "/rates/store/like/{productId}")
    public ResponseEntity<Void> likeComment(@PathVariable Long productId,
                                            @RequestParam Long rateId) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        rateService.likeComment(rateId, productId, userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }

    @PutMapping(value = "/rates/store/dislike/{productId}")
    public ResponseEntity<Void> dislikeComment(@PathVariable Long productId,
                                               @RequestParam Long rateId) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        rateService.dislikeComment(rateId, productId, userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }

    @PutMapping(value = "/rates/store/available/{rateId}")
    public ResponseEntity<Void> likeComment(@PathVariable Long rateId) {
        rateService.updateAvailableComment(rateId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }

    @GetMapping(value = "/rates/store/avg/{productId}")
    public ResponseEntity<Double> getAvgRateStar(@PathVariable Long productId) {
        return ResponseEntity.status(HttpStatus.OK).body(rateService.avgRateStar(productId));
    }

    //info service
    @GetMapping("/rates/build-version")
    public ResponseEntity<String> getBuildVersion() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(build);
    }

    @GetMapping("/rates/java-version")
    public ResponseEntity<String> getJavaVersion() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("JAVA_HOME: " + environment.getProperty("JAVA_HOME"));
    }

    @GetMapping("/rates/contact-info")
    public ResponseEntity<RateContactDto> getRateContactDto() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(rateContactDto);
    }


}
