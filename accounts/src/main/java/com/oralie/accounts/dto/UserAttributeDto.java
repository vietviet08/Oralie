package com.oralie.accounts.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserAttributeDto {
        private String userId;
        private List<String> addressDetail;
        private List<String> city;
        private List<String> phone;
}
