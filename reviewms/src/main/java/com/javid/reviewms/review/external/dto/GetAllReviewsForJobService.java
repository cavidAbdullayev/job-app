package com.javid.reviewms.review.external.dto;


import lombok.Builder;

import java.util.List;

@Builder
public record GetAllReviewsForJobService(
       List<GetReviewForJobService> reviews
) {

}
