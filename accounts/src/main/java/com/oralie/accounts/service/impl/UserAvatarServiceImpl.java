package com.oralie.accounts.service.impl;

import com.oralie.accounts.dto.entity.FileMetadata;
import com.oralie.accounts.dto.entity.request.UserAvatarRequest;
import com.oralie.accounts.dto.entity.response.UserAvatarResponse;
import com.oralie.accounts.exception.ResourceNotFoundException;
import com.oralie.accounts.model.UserAvatar;
import com.oralie.accounts.repository.UserAvatarRepository;
import com.oralie.accounts.service.SocialService;
import com.oralie.accounts.service.UserAvatarService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserAvatarServiceImpl implements UserAvatarService {

    private static final Logger log = LoggerFactory.getLogger(UserAvatarServiceImpl.class);

    private final SocialService socialService;
    private final UserAvatarRepository userAvatarRepository;

    @Override
    public UserAvatarResponse save(String userId, UserAvatarRequest userAvatarRequest) {
        FileMetadata fileMetadata = socialService.uploadImage(userAvatarRequest.getAvatar());
        UserAvatar user = userAvatarRepository.save(UserAvatar.builder()
                .userId(userAvatarRequest.getUserId())
                .name(fileMetadata.getName())
                .size(fileMetadata.getSize())
                .type(fileMetadata.getMime())
                .url(fileMetadata.getUrl())
                .build());
        log.info("UserAvatar: {}", user);
        return mapToUserAvatarResponse(user);
    }


    @Override
    public UserAvatarResponse update(String userId, UserAvatarRequest userAvatarRequest) {
        UserAvatar userAvatar = userAvatarRepository.findByUserId(userId).orElse(new UserAvatar());

        FileMetadata fileMetadata = socialService.uploadImage(userAvatarRequest.getAvatar());

        userAvatar.setUserId(userAvatarRequest.getUserId());
        userAvatar.setName(fileMetadata.getName());
        userAvatar.setSize(fileMetadata.getSize());
        userAvatar.setType(fileMetadata.getMime());
        userAvatar.setUrl(fileMetadata.getUrl());

        return mapToUserAvatarResponse(userAvatarRepository.save(userAvatar));

    }

    @Override
    public UserAvatarResponse findByUserId(String userId) {
        UserAvatar userAvatar = userAvatarRepository.findByUserId(userId).orElseThrow(() -> new ResourceNotFoundException("User not found", "userId", userId));
        return mapToUserAvatarResponse(userAvatar);
    }

    @Override
    public void deleteByUserId(String userId) {
        UserAvatar userAvatar = userAvatarRepository.findByUserId(userId).orElseThrow(() -> new ResourceNotFoundException("User not found", "userId", userId));
        userAvatarRepository.delete(userAvatar);
    }

    private UserAvatarResponse mapToUserAvatarResponse(UserAvatar user) {
        return UserAvatarResponse.builder()
                .id(user.getId())
                .userId(user.getUserId())
                .name(user.getName())
                .size(user.getSize())
                .type(user.getType())
                .url(user.getUrl())
                .build();
    }
}
