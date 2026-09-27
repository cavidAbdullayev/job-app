package com.javid.reviewms.review.dto;

public record UpdateReviewRequestDto(
        String title,
        String description,
        Double rating
) {

}
