package com.javid.userservice.dto;

import lombok.Builder;

@Builder
public record UpdateUserResponse(
        String firstName,
        String lastName,
        String phone
) {
}
