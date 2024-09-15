package com.oralie.accounts.dto.identity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AssignRole {
    private String name;
    private String description;
    private boolean clientRole;
}
