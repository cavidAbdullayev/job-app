package com.javid.reviewms.review.mapper.impl;

import com.javid.reviewms.review.entity.Review;
import com.javid.reviewms.review.dto.CreateReviewRequestDto;
import com.javid.reviewms.review.dto.GetReviewResponse;
import com.javid.reviewms.review.dto.UpdateReviewRequestDto;
import com.javid.reviewms.review.external.dto.GetAllReviewsForJobService;
import com.javid.reviewms.review.external.dto.GetReviewForJobService;
import com.javid.reviewms.review.mapper.ReviewMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewMapperImpl implements ReviewMapper {


    @Override
    public void mapForUpdateRequest(Review review, UpdateReviewRequestDto updatedReview) {
        if(updatedReview.description() != null)
            review.setDescription(updatedReview.description());
        if(updatedReview.rating() != null)
            review.setRating(updatedReview.rating());
        if(updatedReview.title() != null)
            review.setTitle(updatedReview.title());
    }

    @Override
    public Review mapFromCreateRequest(CreateReviewRequestDto createdReview) {
        return Review.builder()
                .description(createdReview.description())
                .rating(createdReview.rating())
                .title(createdReview.title())
                .build();
    }

    @Override
    public GetReviewResponse mapToResponse(Review review) {
        return GetReviewResponse.builder()
                .description(review.getDescription())
                .rating(review.getRating())
                .title(review.getTitle())
                .build();
    }

    @Override
    public GetAllReviewsForJobService mapToGetAllReviewsForJobService(List<Review> reviews) {
        return GetAllReviewsForJobService.builder()
                .reviews(
                        reviews.stream().map(this::mapToGetReviewForJobService).toList()
                ).build();
    }

    private GetReviewForJobService mapToGetReviewForJobService(Review review){
        return GetReviewForJobService.builder()
                .description(review.getDescription())
                .title(review.getTitle())
                .rating(review.getRating())
                .build();
    }
}
