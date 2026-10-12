package com.javid.userservice.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateUserRequest(
        @Size(max = 25, message = "First name cannot exceed 25 characters")
        String firstName,
        @Size(max = 25, message = "Last name cannot exceed 25 characters")
        String lastName,
        @Pattern(
                regexp = "^$|^\\+?[1-9]\\d{1,14}$",
                message = "Please enter a valid phone number (e.g. +994501234567)"
        )
        String phone
) {
}