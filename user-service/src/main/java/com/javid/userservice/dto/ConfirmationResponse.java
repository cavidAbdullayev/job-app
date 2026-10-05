package com.javid.userservice.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ConfirmationResponse(
        String message,
        String email,
        boolean isConfirmed,
        LocalDateTime confirmedAt
) {}