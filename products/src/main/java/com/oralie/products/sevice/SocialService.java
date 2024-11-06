package com.oralie.products.sevice;

import com.oralie.products.model.s3.FileMetadata;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.net.URI;
import java.util.List;


@Service
@RequiredArgsConstructor
public class SocialService extends AbstractCircuitBreakFallbackHandler {
    private final RestClient restClient;
    private final RestTemplate restTemplate;

    private static final String URL_SOCIAL = "http://localhost:8086";

    @Retry(name = "restRetry")
    @CircuitBreaker(name = "restCircuitBreaker", fallbackMethod = "handleFileMetadataFallback")
    public FileMetadata uploadImage(MultipartFile image) {
        final String jwt = ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .getTokenValue();
        final URI url = UriComponentsBuilder
                .fromHttpUrl(URL_SOCIAL)
                .path("/dash/social/upload-image")
                .buildAndExpand()
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwt);
//        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("image", image.getResource());

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<FileMetadata> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                FileMetadata.class
        );

        return response.getBody();
    }

    @Retry(name = "restRetry")
    @CircuitBreaker(name = "restCircuitBreaker", fallbackMethod = "handleFileMetadataFallback")
    public List<FileMetadata> uploadImages(List<MultipartFile> images) {
        final String jwt = ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .getTokenValue();
        final URI url = UriComponentsBuilder
                .fromHttpUrl(URL_SOCIAL)
                .path("/dash/social/upload-images")
                .buildAndExpand()
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwt);
//        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        for (MultipartFile image : images) {
            body.add("images", image.getResource());
        }

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<List<FileMetadata>> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                new ParameterizedTypeReference<List<FileMetadata>>() {
                }
        );

        return response.getBody();
    }

    @Retry(name = "restRetry")
    @CircuitBreaker(name = "restCircuitBreaker", fallbackMethod = "handleStringFallback")
    public String deleteFile(String fileName) {
        final String jwt = ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .getTokenValue();
        final URI url = UriComponentsBuilder
                .fromHttpUrl(URL_SOCIAL)
                .path("/dash/social/delete/{fileName}")
                .buildAndExpand(fileName)
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwt);

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.DELETE,
                requestEntity,
                String.class
        );

        return response.getBody();
    }

    protected List<FileMetadata> handleFileMetadataFallback(Throwable throwable) throws Throwable {
        return handleTypedFallback(throwable);
    }

    protected List<FileMetadata> handleStringFallback(Throwable throwable) throws Throwable {
        return handleTypedFallback(throwable);
    }
}
