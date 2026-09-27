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
    boolean addReview(Long companyId, CreateReviewRequestDto reviewDto);
    GetReviewResponse getReview(Long reviewId);
    boolean updateReview(Long reviewId, UpdateReviewRequestDto updatedReview);

    boolean deleteReview(Long reviewId);
    GetAllReviewsForJobService getAllReviewsForJobService(Long companyId);
}
