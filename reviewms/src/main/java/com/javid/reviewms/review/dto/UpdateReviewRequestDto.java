package com.javid.reviewms.review.dto;

import jakarta.validation.constraints.*;

public record UpdateReviewRequestDto(
        @Size(max = 100, message = "Title must not exceed 100 characters")
        @NotEmpty(message = "Title cannot be empty")
        String title,
        @Size(max = 100, message = "Title must not exceed 100 characters")
        @NotEmpty(message = "Description cannot be empty")
        String description,
        @Min(value = 0, message = "Rating cannot be less than 0")
        @Max(value = 100, message = "Rating cannot be greater than 100")
        Double rating
) {

}
