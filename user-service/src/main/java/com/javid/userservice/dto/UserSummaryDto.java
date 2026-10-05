package com.javid.userservice.dto;

import com.javid.userservice.enums.UserStatusEnum;
import lombok.Builder;

import java.util.UUID;

@Builder
public record UserSummaryDto(
        UUID id,
        String username,
        String email,
        String firstName,
        String lastName,
        UserStatusEnum statusEnum
) {
}
