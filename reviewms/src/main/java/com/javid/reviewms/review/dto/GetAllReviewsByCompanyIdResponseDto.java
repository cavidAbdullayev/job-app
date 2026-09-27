package com.javid.reviewms.review.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record GetAllReviewsByCompanyIdResponseDto(
        List<GetReviewResponse> reviewResponses,
        String companyName,
        Double companyRating
) {

}
