package com.oralie.rates.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserInfoRequest {
    private String userId;
    private String fullName;
    private String urlAvatar;
}
