package com.example.aaugp.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenRequest {

    @NotBlank
    @Schema(
            description = "Refresh token returned by login, register, or the previous refresh call.",
            example = "replace-with-refresh-token")
    private String refreshToken;
}
