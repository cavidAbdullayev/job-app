package com.javid.userservice.service;

import com.javid.userservice.dto.*;

public interface UserService {
    UserRegisterResponse register(UserRegisterRequest request);
    ConfirmationResponse confirmRegistration(String token);

    UpdateUserResponse updateUser(String token, UpdateUserRequest request);
}
