package com.javid.reviewms.review.mapper;

import com.javid.reviewms.review.entity.Review;
import com.javid.reviewms.review.dto.CreateReviewRequestDto;
import com.javid.reviewms.review.dto.GetReviewResponse;
import com.javid.reviewms.review.dto.UpdateReviewRequestDto;
import com.javid.reviewms.review.external.dto.GetAllReviewsForJobService;

import java.util.List;

public interface ReviewMapper {
    void mapForUpdateRequest(Review review, UpdateReviewRequestDto updatedReview);
    Review mapFromCreateRequest(CreateReviewRequestDto createdReview);
    GetReviewResponse mapToResponse(Review review);
    GetAllReviewsForJobService mapToGetAllReviewsForJobService(List<Review> reviews);

}
