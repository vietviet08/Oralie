package com.oralie.accounts.dto.entity.response;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name = "Response", description = "Schema to hold successful response information")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseDto {

    @Schema(description = "Status code in the response")
    private String statusCode;

    @Schema(description = "Status message in the response")
    private String statusMessage;

}