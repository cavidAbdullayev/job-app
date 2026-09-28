package com.javid.companyms.company.dto.messaging;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ReviewMessage(
        @NotNull(message = "Average rating is required")
        @Min(value = 0, message = "Average rating cannot be less than 0")
        @Max(value = 0, message = "Average rating cannot be greater than 100")
        Double averageRating,
        @NotNull(message = "Average rating is required")
        Long companyId
){}