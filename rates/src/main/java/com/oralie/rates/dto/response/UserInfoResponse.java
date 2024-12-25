package com.oralie.rates.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserInfoResponse {
    private String userId;
    private String fullName;
    private String urlAvatar;
}
