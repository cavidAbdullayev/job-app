package com.javid.reviewms.review.service;

import com.javid.reviewms.review.dto.CreateReviewRequestDto;
import com.javid.reviewms.review.dto.GetAllReviewsByCompanyIdResponseDto;
import com.javid.reviewms.review.dto.GetReviewResponse;
import com.javid.reviewms.review.dto.UpdateReviewRequestDto;
import com.javid.reviewms.review.external.dto.GetAllReviewsForJobService;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface ReviewService {
    GetAllReviewsByCompanyIdResponseDto getAllReviews(Long companyId);
    GetReviewResponse addReview(Long companyId, CreateReviewRequestDto reviewDto);
    GetReviewResponse getReview(Long reviewId);
    GetReviewResponse updateReview(Long reviewId, UpdateReviewRequestDto updatedReview);

    void deleteReview(Long reviewId);
    GetAllReviewsForJobService getAllReviewsForJobService(Long companyId);
    Double getAverageRating(Long companyId);
}
