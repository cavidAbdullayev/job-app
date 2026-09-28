package com.javid.reviewms.review.dto;

import jakarta.validation.constraints.*;

public record CreateReviewRequestDto(
        @Size(max = 100, message = "Title must not exceed 100 characters")
                @NotBlank(message = "Title is required!")
        String title,
        @Size(max = 100, message = "Title must not exceed 100 characters")
        @NotBlank(message = "Title is required!")
        String description,
        @NotNull(message = "Rating is required")
        @Min(value = 0, message = "Rating cannot be less than 0")
        @Max(value = 100, message = "Rating cannot be greater than 100")
        double rating
) {

}
