package com.javid.reviewms.review.external.dto;

import lombok.Builder;

@Builder
public record GetReviewForJobService(
        String title,
        String description,
        Double rating
) {

}
