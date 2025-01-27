package com.oralie.accounts.dto.entity.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserAttributeResponse {
    private Long id;
    private String userId;
    private List<String> address;
    private List<String> city;
    private List<String> phone;
    private List<String> picture;
}