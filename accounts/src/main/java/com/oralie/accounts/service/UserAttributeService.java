package com.oralie.accounts.service;

import com.oralie.accounts.dto.entity.request.UserAttributeRequest;
import com.oralie.accounts.dto.entity.response.UserAttributeResponse;

import java.util.List;

public interface UserAttributeService {
    UserAttributeResponse findById(Long userAttributeId);

    UserAttributeResponse save(UserAttributeRequest userAttributeRequest);

    UserAttributeResponse update(UserAttributeRequest userAttributeRequest, String userId);

    void deleteById(Long userAttributeId);

    void deleteByUserId(String userId);

    void deleteByUsername(String username);

    UserAttributeResponse findAllByUserId(String userId);

    List<UserAttributeResponse> findAll(int page, int size, String sortBy, String sort);

}
