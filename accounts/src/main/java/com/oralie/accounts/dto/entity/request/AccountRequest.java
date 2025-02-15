package com.oralie.accounts.dto.entity.request;

import com.oralie.accounts.dto.ValidationGroups;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Schema(name = "AccountRequest", description = "Schema to hold request account information")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountRequest {

    @Size(min = 4, max = 20, message = "Username must be between 4 and 20 characters")
    @NotBlank(message = "Username is required", groups = {ValidationGroups.OnCreate.class})
    private String username;

    @Size(min = 6, max = 20, message = "Password must be between 6 and 20 characters")
    @NotBlank(message = "Password is required", groups = {ValidationGroups.OnCreate.class})
    private String password;

    @NotBlank(message = "Email is required")
    @Email(message = "Email is invalid")
    private String email;

    private UserAttributeRequest userAttribute;

    private String firstName;
    private String lastName;
    private Boolean gender;

}
