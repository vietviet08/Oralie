package com.oralie.accounts.dto.identity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAttribute {
   private List<String> address;
   private List<String> phone;
   private List<String> city;
   private List<String> picture;
}
