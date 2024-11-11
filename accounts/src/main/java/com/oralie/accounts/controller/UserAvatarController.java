package com.oralie.accounts.controller;

import com.oralie.accounts.dto.entity.request.UserAvatarRequest;
import com.oralie.accounts.dto.entity.response.UserAvatarResponse;
import com.oralie.accounts.service.UserAvatarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class UserAvatarController {

    private final UserAvatarService userAvatarService;

    @GetMapping("/accounts/user-avatar")
    public ResponseEntity<UserAvatarResponse> getUserAvatar() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userAvatarService.findByUserId(userId));
    }

    @PostMapping("/accounts/user-avatar")
    public ResponseEntity<UserAvatarResponse> saveUserAvatar(@ModelAttribute UserAvatarRequest userAvatarRequest) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userAvatarService.save(userId, userAvatarRequest));
    }

    @PutMapping("/accounts/user-avatar")
    public ResponseEntity<UserAvatarResponse> updateUserAvatar(@ModelAttribute UserAvatarRequest userAvatarRequest) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userAvatarService.update(userId, userAvatarRequest));
    }

    @DeleteMapping("/accounts/user-avatar")
    public ResponseEntity<Void> deleteUserAvatar() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        userAvatarService.deleteByUserId(userId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

}
