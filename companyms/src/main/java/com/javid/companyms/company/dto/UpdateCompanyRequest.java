package com.javid.companyms.company.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateCompanyRequest(
        @Size(max = 100, message = "Company name must not exceed 100 characters")
        @NotEmpty(message = "Company name cannot be empty")
        String name,

        @Size(max = 500, message = "Company description must not exceed 500 characters")
        @NotEmpty(message = "Company description cannot be empty")
        String description
) {

}
