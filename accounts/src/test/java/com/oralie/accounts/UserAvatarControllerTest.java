package com.oralie.accounts;

import com.oralie.accounts.controller.UserAvatarController;
import com.oralie.accounts.dto.entity.response.UserAvatarResponse;
import com.oralie.accounts.service.UserAvatarService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class UserAvatarControllerTest {

    @Mock
    private UserAvatarService userAvatarService;

    @InjectMocks
    private UserAvatarController userAvatarController;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void getUserAvatar() {
        // Arrange
        String userId = "c65d0a6f-9a8a-4fcd-8687-a183b226d636";
        UserAvatarResponse userAvatarResponse = new UserAvatarResponse();
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(userId);
        when(userAvatarService.findByUserId(userId)).thenReturn(userAvatarResponse);

        // Act
        ResponseEntity<UserAvatarResponse> responseEntity = userAvatarController.getUserAvatar();

        // Assert
        assertEquals(ResponseEntity.ok(userAvatarResponse), responseEntity);
        verify(userAvatarService, times(1)).findByUserId(userId);
    }
}