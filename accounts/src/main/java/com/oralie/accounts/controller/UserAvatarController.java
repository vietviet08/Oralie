package com.oralie.accounts.controller;

import com.oralie.accounts.dto.entity.request.UserAvatarRequest;
import com.oralie.accounts.dto.entity.response.UserAvatarResponse;
import com.oralie.accounts.service.UserAvatarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for managing user avatars.
 */
@Tag(name = "User Avatar Controller", description = "Controller for managing user avatars.")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequiredArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class UserAvatarController {

    private final UserAvatarService userAvatarService;

    /**
     * Retrieves the avatar of the currently authenticated user.
     *
     * @return ResponseEntity containing the user's avatar.
     */
    @Operation(summary = "Get User Avatar", description = "Retrieves the avatar of the currently authenticated user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the user's avatar"),
            @ApiResponse(responseCode = "401", description = "Unauthorized to access this resource"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/accounts/profile/image")
    public ResponseEntity<UserAvatarResponse> getUserAvatar() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        if (userId == null) {
            throw new IllegalStateException("User ID not found in security context");
        }
        return ResponseEntity
                .ok(userAvatarService.findByUserId(userId));
    }

    /**
     * Saves a new avatar for the currently authenticated user.
     *
     * @param userAvatarRequest The request containing the avatar data.
     * @return ResponseEntity containing the saved avatar.
     */
    @Operation(summary = "Save User Avatar", description = "Saves a new avatar for the currently authenticated user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully saved the user's avatar"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @ApiResponse(responseCode = "401", description = "Unauthorized to access this resource"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/accounts/profile/image")
    public ResponseEntity<UserAvatarResponse> saveUserAvatar(@RequestBody UserAvatarRequest userAvatarRequest) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        if (userId == null) {
            throw new IllegalStateException("User ID not found in security context");
        }
        UserAvatarResponse response = userAvatarService.save(userId, userAvatarRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    /**
     * Updates the avatar of the currently authenticated user.
     *
     * @param userAvatarRequest The request containing the updated avatar data.
     * @return ResponseEntity containing the updated avatar.
     */
    @Operation(summary = "Update User Avatar", description = "Updates the avatar of the currently authenticated user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated the user's avatar"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @ApiResponse(responseCode = "401", description = "Unauthorized to access this resource"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/accounts/profile/image")
    public ResponseEntity<UserAvatarResponse> updateUserAvatar(@RequestBody UserAvatarRequest userAvatarRequest) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        if (userId == null) {
            throw new IllegalStateException("User ID not found in security context");
        }
        UserAvatarResponse response = userAvatarService.update(userId, userAvatarRequest);
        return ResponseEntity
                .ok(response);
    }

    /**
     * Deletes the avatar of the currently authenticated user.
     *
     * @return ResponseEntity with no content.
     */

    @Operation(summary = "Delete User Avatar", description = "Deletes the avatar of the currently authenticated user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully deleted the user's avatar"),
            @ApiResponse(responseCode = "401", description = "Unauthorized to access this resource"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/accounts/profile/image")
    public ResponseEntity<Void> deleteUserAvatar() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        if (userId == null) {
            throw new IllegalStateException("User ID not found in security context");
        }
        userAvatarService.deleteByUserId(userId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}