package com.javid.jobms.job.dto.message;

import lombok.Builder;

@Builder
public record ReviewResponse(
        String title,
        String description,
        Double rating
) {
}
