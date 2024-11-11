package com.oralie.accounts.service;

import com.oralie.accounts.dto.entity.request.UserAvatarRequest;
import com.oralie.accounts.dto.entity.response.UserAvatarResponse;

public interface UserAvatarService {
    UserAvatarResponse save(String userId, UserAvatarRequest userAvatarRequest);

    UserAvatarResponse update(String userId, UserAvatarRequest userAvatarRequest);

    UserAvatarResponse findByUserId(String userId);

    void deleteByUserId(String userId);
}
