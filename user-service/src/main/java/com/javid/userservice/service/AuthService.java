package com.javid.userservice.service;

import com.javid.userservice.dto.AuthResponse;
import com.javid.userservice.dto.LoginRequest;
import com.javid.userservice.dto.RefreshTokenRequest;

public interface AuthService {
    AuthResponse login(LoginRequest request);

    AuthResponse refreshToken(RefreshTokenRequest request);
}