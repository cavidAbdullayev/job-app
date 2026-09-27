package com.javid.reviewms.review.service.impl;


import com.javid.reviewms.review.entity.Review;
import com.javid.reviewms.review.external.dto.GetAllReviewsForJobService;
import com.javid.reviewms.review.external.service.ExternalCompanyService;
import com.javid.reviewms.review.repository.ReviewRepository;
import com.javid.reviewms.review.service.ReviewService;
import com.javid.reviewms.review.dto.CreateReviewRequestDto;
import com.javid.reviewms.review.dto.GetAllReviewsByCompanyIdResponseDto;
import com.javid.reviewms.review.dto.GetReviewResponse;
import com.javid.reviewms.review.dto.UpdateReviewRequestDto;
import com.javid.reviewms.review.mapper.ReviewMapper;
import com.javid.reviewms.review.messaging.ReviewMessageProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final ReviewMessageProducer reviewMessageProducer;
    private final ReviewMapper reviewMapper;
    private final ExternalCompanyService externalCompanyService;


    @Override
    @Cacheable(value = "allReviews", key = "#companyId")
    public GetAllReviewsByCompanyIdResponseDto getAllReviews(Long companyId) {
        log.info("getAllReviews method started");
        List<Review> reviews = reviewRepository.findByCompanyId(companyId);
        log.info("all reviews retrieved by size: "+reviews.size());
        List<GetReviewResponse> reviewResponses = reviews.stream().map(reviewMapper::mapToResponse).toList();

        log.info("company service is calling...");
        String companyName = externalCompanyService.getCompanyResponseForReview(companyId).companyName();
        Double companyRating = reviewRepository.getAvgRatingByCompanyId(companyId);
        log.info("company service called: {} - {}", companyName, companyRating);

        return GetAllReviewsByCompanyIdResponseDto.builder()
                .reviewResponses(reviewResponses)
                .companyName(companyName)
                .companyRating(companyRating)
                .build();
    }

    @Override
    @CacheEvict(value = "allReviews", allEntries = true)
    public boolean addReview(Long companyId, CreateReviewRequestDto reviewDto) {
        if (companyId != null) {
            if(!checkCompanyExists(companyId)){
                //TODO: throw ex
                return false;
            }

            Review review = reviewMapper.mapFromCreateRequest(reviewDto);
            review.setCompanyId(companyId);

            reviewRepository.save(review);
            //TODO: will be outbox
            reviewMessageProducer.sendMessage(companyId);
            return true;
        }
        return false;
    }

    private boolean checkCompanyExists(Long companyId){
        if(companyId != null)
            return externalCompanyService.existsCompanyForReview(companyId);
        return false;
    }

    @Override
    @Cacheable(value = "reviews", key = "#reviewId")
    public GetReviewResponse getReview(Long reviewId) {
        if(reviewRepository.existsById(reviewId))
            return reviewMapper.mapToResponse(reviewRepository.findById(reviewId).orElse(null));
        return null;
    }

    @Override
    @Caching(
            evict = {
                    @CacheEvict(value = "reviews", key = "#reviewId"),
                    @CacheEvict(value = "allReviews", allEntries = true)
            }
    )
    public boolean updateReview(Long reviewId, UpdateReviewRequestDto updatedReview) {
        Review review = reviewRepository.findById(reviewId).orElse(null);
        if (review != null) {
            reviewMapper.mapForUpdateRequest(review, updatedReview);
            reviewRepository.save(review);

            //TODO: will be outbox
            reviewMessageProducer.sendMessage(review.getCompanyId());
            return true;
        }
        return false;
    }

    @Override
    @Caching(
            evict = {
                    @CacheEvict(value = "reviews", key = "#reviewId"),
                    @CacheEvict(value = "allReviews", allEntries = true)
            }
    )
    public boolean deleteReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId).orElse(null);
        if (review != null) {
            reviewRepository.delete(review);

            //TODO: will be outbox
            reviewMessageProducer.sendMessage(review.getCompanyId());
            return true;
        }
        return false;
    }

    @Override
    public GetAllReviewsForJobService getAllReviewsForJobService(Long companyId) {
        List<Review> reviews = reviewRepository.findByCompanyId(companyId);
        return reviewMapper.mapToGetAllReviewsForJobService(reviews);
    }
}