package com.javid.companyms.company.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateCompanyRequest(
        @Size(max = 100, message = "Company name must not exceed 100 characters")
        @NotBlank(message = "Company name is required")
        String name,
        @Size(max = 500, message = "Company description must not exceed 500 characters")
        @NotBlank(message = "Company description is required")
        String description
) {
}
