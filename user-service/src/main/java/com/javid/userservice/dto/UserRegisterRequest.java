package com.javid.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;


public record UserRegisterRequest(

        @NotBlank(message = "Username cannot be blank")
        @Size(min = 3, max = 25, message = "Username must be between 3 and 25 characters")
        @Pattern(
                regexp = "^[a-zA-Z0-9_.]+$",
                message = "Username can only contain letters, numbers, underscores, and dots"
        )
        String username,

        @NotBlank(message = "Email address cannot be blank")
        @Email(
                regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
                message = "Please enter a valid email address"
        )
        @Size(max = 50, message = "Email cannot exceed 50 characters")
        String email,

        @NotBlank(message = "Password cannot be blank")
        @Size(min = 8, max = 64, message = "Password must be between 8 and 64 characters")
        @Pattern(
                regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!._-]).{8,}$",
                message = "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character"
        )
        String password,

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