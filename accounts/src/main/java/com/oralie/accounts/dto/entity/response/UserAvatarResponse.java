package com.oralie.accounts.dto.entity.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserAvatarResponse {
    private Long id;
    private String userId;
    private String name;
    private Long size;
    private String type;
    private String url;
}
