package com.javid.jobms.job.external.dto;

import lombok.Builder;

@Builder
public record GetReviewForJobService(
        String title,
        String description,
        Double rating
) {
}
