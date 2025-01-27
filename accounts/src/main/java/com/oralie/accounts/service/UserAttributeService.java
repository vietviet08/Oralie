package com.oralie.accounts.service;

import com.oralie.accounts.dto.identity.UserAttribute;

public interface UserAttributeService {
    UserAttribute getUserAttributeByUserId(String userId);

    UserAttribute save(UserAttribute userAttribute, String userId);

    UserAttribute update(UserAttribute userAttribute, String userId);

    void deleteByUserId(String userId);
}
