package com.oralie.accounts.dto.entity.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserAttributeRequest {
    private List<String> address;
    private List<String> city;
    private List<String> phone;
    private MultipartFile picture;
}
