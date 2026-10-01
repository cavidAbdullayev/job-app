package com.javid.userservice.service;

import com.javid.userservice.dto.UserRegisterRequest;

public interface UserService {
    String register(UserRegisterRequest request);
    String confirmRegistration(String token);
}
