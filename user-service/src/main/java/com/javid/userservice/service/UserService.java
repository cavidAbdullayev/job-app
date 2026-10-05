package com.javid.userservice.service;

import com.javid.userservice.dto.ConfirmationResponse;
import com.javid.userservice.dto.UserRegisterRequest;
import com.javid.userservice.dto.UserRegisterResponse;

public interface UserService {
    UserRegisterResponse register(UserRegisterRequest request);
    ConfirmationResponse confirmRegistration(String token);

}
