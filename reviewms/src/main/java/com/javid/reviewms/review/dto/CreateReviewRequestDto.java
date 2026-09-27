package com.javid.reviewms.review.dto;

public record CreateReviewRequestDto(
        String title,
        String description,
        double rating
) {

}
