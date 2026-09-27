package com.javid.reviewms.review.dto;

import lombok.Builder;

@Builder
public record GetReviewResponse(
        String title,
        String description,
        double rating
) {

}
