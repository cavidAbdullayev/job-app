package com.javid.userservice.dto;

import com.javid.userservice.enums.UserStatusEnum;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record UserRegisterResponse(
        String message,
        String email,
        String firstName,
        String lastName,
        String phone
) {}
