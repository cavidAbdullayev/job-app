package com.javid.userservice.service.impl;

import com.javid.userservice.dto.AuthResponse;
import com.javid.userservice.dto.LoginRequest;
import com.javid.userservice.dto.RefreshTokenRequest;
import com.javid.userservice.dto.UserSummaryDto;
import com.javid.userservice.entity.RefreshTokenEntity;
import com.javid.userservice.entity.UserEntity;
import com.javid.userservice.repository.RefreshTokenRepository;
import com.javid.userservice.repository.UserRepository;
import com.javid.userservice.service.AuthService;
import com.javid.userservice.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Value("${app.jwt.refresh-token-expiration-ms}")
    private long refreshTokenExpirationMs;

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {

        UserEntity user = userRepository.findByUsernameOrEmailActive(request.usernameOrEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        refreshTokenRepository.revokeAllUserTokens(user);

        String accessToken = jwtUtil.generateAccessToken(user);
        RefreshTokenEntity refreshToken = createRefreshToken(user);


        UserSummaryDto userSummary = UserSummaryDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .statusEnum(user.getStatus())
                .build();

        return new AuthResponse(
                accessToken,
                refreshToken.getToken(),
                "Bearer",
                jwtUtil.getAccessTokenExpirationMs() / 1000,
                userSummary
        );
    }

    @Override
    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {

        RefreshTokenEntity refreshToken = refreshTokenRepository.findByToken(request.refreshToken())
                .orElseThrow(() -> new BadCredentialsException("Invalid refresh token"));

        if (refreshToken.isRevoked() || refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BadCredentialsException("Refresh token is expired or revoked");
        }

        UserEntity user = refreshToken.getUser();

        refreshToken.setRevoked(true);

        refreshTokenRepository.save(refreshToken);

        String newAccessToken = jwtUtil.generateAccessToken(user);
        RefreshTokenEntity newRefreshToken = createRefreshToken(user);

        UserSummaryDto userSummary = UserSummaryDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .statusEnum(user.getStatus())
                .build();

        return new AuthResponse(
                newAccessToken,
                newRefreshToken.getToken(),
                "Bearer",
                jwtUtil.getAccessTokenExpirationMs() / 1000,
                userSummary
        );
    }

    private RefreshTokenEntity createRefreshToken(UserEntity user) {
        RefreshTokenEntity refreshToken = RefreshTokenEntity.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiresAt(LocalDateTime.now().plusSeconds(refreshTokenExpirationMs / 1000))
                .revoked(false)
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

}