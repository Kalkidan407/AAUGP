package com.example.aaugp.services;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.aaugp.model.RefreshTokenEntity;
import com.example.aaugp.model.UserEntity;
import com.example.aaugp.repositories.RefreshTokenRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private static final int REFRESH_TOKEN_BYTES = 64;

    private final RefreshTokenRepository refreshTokenRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    @Value("${app.jwt.refresh-token-expiration-seconds:2592000}")
    private long refreshTokenExpirationSeconds;

    @Transactional
    public IssuedRefreshToken issueToken(UserEntity user) {
        Instant now = Instant.now();
        String token = generateTokenValue();

        RefreshTokenEntity refreshToken = new RefreshTokenEntity();
        refreshToken.setTokenHash(hash(token));
        refreshToken.setUser(user);
        refreshToken.setCreatedAt(now);
        refreshToken.setExpiresAt(now.plusSeconds(refreshTokenExpirationSeconds));

        refreshTokenRepository.save(refreshToken);
        return new IssuedRefreshToken(token, refreshToken.getExpiresAt());
    }

    @Transactional
    public RotatedRefreshToken rotateToken(String token) {
        RefreshTokenEntity existingToken = requireActiveToken(token);
        existingToken.setRevokedAt(Instant.now());
        IssuedRefreshToken newToken = issueToken(existingToken.getUser());
        return new RotatedRefreshToken(newToken.token(), newToken.expiresAt(), existingToken.getUser());
    }

    @Transactional
    public void revokeToken(String token) {
        RefreshTokenEntity refreshToken = requireActiveToken(token);
        refreshToken.setRevokedAt(Instant.now());
    }

    @Transactional
    @Scheduled(fixedDelayString = "${app.jwt.refresh-token-cleanup-delay-ms:3600000}")
    public void deleteExpiredTokens() {
        refreshTokenRepository.deleteByExpiresAtBefore(Instant.now());
    }

    private RefreshTokenEntity requireActiveToken(String token) {
        if (token == null || token.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
        }

        Instant now = Instant.now();
        RefreshTokenEntity refreshToken = refreshTokenRepository.findByTokenHash(hash(token))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token"));

        if (!refreshToken.isActive(now)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
        }

        return refreshToken;
    }

    private String generateTokenValue() {
        byte[] bytes = new byte[REFRESH_TOKEN_BYTES];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hash(String token) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(token.getBytes(StandardCharsets.UTF_8));
            StringBuilder hash = new StringBuilder(digest.length * 2);
            for (byte value : digest) {
                hash.append(String.format("%02x", value));
            }
            return hash.toString();
        } catch (Exception exception) {
            throw new IllegalStateException("Could not hash refresh token", exception);
        }
    }

    public record IssuedRefreshToken(String token, Instant expiresAt) {
    }

    public record RotatedRefreshToken(String token, Instant expiresAt, UserEntity user) {
    }
}
