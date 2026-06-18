package com.example.aaugp.services;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.aaugp.dto.auth.AuthRequest;
import com.example.aaugp.dto.auth.AuthResponse;
import com.example.aaugp.dto.auth.RefreshTokenRequest;
import com.example.aaugp.dto.auth.RegisterRequest;
import com.example.aaugp.dto.user.UserRequest;
import com.example.aaugp.dto.user.UserResponse;
import com.example.aaugp.model.UserEntity;
import com.example.aaugp.repositories.UserRepository;
import com.example.aaugp.services.RefreshTokenService.IssuedRefreshToken;
import com.example.aaugp.services.RefreshTokenService.RotatedRefreshToken;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserServices userServices;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        UserResponse user = userServices.createUser(toUserRequest(request));
        UserEntity savedUser = userRepository.findByEmailIgnoreCase(user.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "User registration failed"));
        return createAuthResponse(savedUser);
    }

    @Transactional
    public AuthResponse login(AuthRequest request) {
        String email = request.getEmail() == null ? "" : request.getEmail().trim();
        UserEntity user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));

        if (!isPasswordValid(request.getPassword(), user)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }

        return createAuthResponse(user);
    }

    @Transactional
    public AuthResponse refresh(RefreshTokenRequest request) {
        RotatedRefreshToken refreshToken = refreshTokenService.rotateToken(request.getRefreshToken());
        return createAuthResponse(refreshToken.user(), refreshToken.token());
    }

    @Transactional
    public void logout(RefreshTokenRequest request) {
        refreshTokenService.revokeToken(request.getRefreshToken());
    }

    private boolean isPasswordValid(String rawPassword, UserEntity user) {
        if (passwordEncoder.matches(rawPassword, user.getPassword())) {
            return true;
        }

        if (rawPassword != null && rawPassword.equals(user.getPassword())) {
            user.setPassword(passwordEncoder.encode(rawPassword));
            userRepository.save(user);
            return true;
        }

        return false;
    }

    private AuthResponse createAuthResponse(UserEntity user) {
        IssuedRefreshToken refreshToken = refreshTokenService.issueToken(user);
        return createAuthResponse(user, refreshToken.token());
    }

    private AuthResponse createAuthResponse(UserEntity user, String refreshToken) {
        return new AuthResponse(
                jwtService.generateToken(user),
                refreshToken,
                "Bearer",
                jwtService.getExpirationSeconds(),
                userServices.toDTO(user));
    }

    private UserRequest toUserRequest(RegisterRequest request) {
        String normalizedName = request.getName().trim().replaceAll("\\s+", " ");
        String[] nameParts = normalizedName.split(" ", 2);
        String name = nameParts[0];
   

        return new UserRequest(
                name,
                request.getStudentId(),
                request.getEmail(),
                request.getPassword(),
                request.getDepartment());
    }
}
