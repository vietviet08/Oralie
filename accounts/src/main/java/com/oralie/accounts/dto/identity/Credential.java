package com.oralie.accounts.dto.identity;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Credential {
    String type;
    String userLabel;
    String secretData;
    String credentialData;
    String value;
    boolean temporary;
}
