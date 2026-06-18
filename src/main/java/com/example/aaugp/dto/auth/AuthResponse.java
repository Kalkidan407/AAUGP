package com.example.aaugp.dto.auth;

import com.example.aaugp.dto.user.UserResponse;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    @Schema(description = "Deprecated alias for accessToken.", example = "jwt-access-token")
    private String token;

    @Schema(description = "Short-lived JWT used in the Authorization header.", example = "jwt-access-token")
    private String accessToken;

    @Schema(description = "Long-lived opaque token used to request a new access token.", example = "opaque-refresh-token")
    private String refreshToken;

    @Schema(description = "Authorization scheme for the access token.", example = "Bearer")
    private String tokenType;

    @Schema(description = "Access token lifetime in seconds.", example = "900")
    private long expiresIn;

    private UserResponse user;

    public AuthResponse(String accessToken, String refreshToken, String tokenType, long expiresIn, UserResponse user) {
        this.token = accessToken;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
        this.user = user;
    }
}
