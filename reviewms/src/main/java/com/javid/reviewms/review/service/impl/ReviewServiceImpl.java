package com.javid.reviewms.review.service.impl;


import com.javid.reviewms.review.dto.CreateReviewRequestDto;
import com.javid.reviewms.review.dto.GetAllReviewsByCompanyIdResponseDto;
import com.javid.reviewms.review.dto.GetReviewResponse;
import com.javid.reviewms.review.dto.UpdateReviewRequestDto;
import com.javid.reviewms.review.entity.Review;
import com.javid.reviewms.review.exception.CompanyNotFoundException;
import com.javid.reviewms.review.exception.ReviewNotFoundException;
import com.javid.reviewms.review.external.dto.GetAllReviewsForJobService;
import com.javid.reviewms.review.external.dto.GetCompanyResponseForReview;
import com.javid.reviewms.review.external.service.ExternalCompanyService;
import com.javid.reviewms.review.mapper.ReviewMapper;
import com.javid.reviewms.review.messaging.ReviewMessageProducer;
import com.javid.reviewms.review.repository.ReviewRepository;
import com.javid.reviewms.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final ReviewMessageProducer reviewMessageProducer;
    private final ReviewMapper reviewMapper;
    private final ExternalCompanyService externalCompanyService;
    private final CacheManager cacheManager;


    @Override
    @Cacheable(value = "allReviews", key = "#companyId", unless = "#result == null")
    @Transactional(readOnly = true)
    public GetAllReviewsByCompanyIdResponseDto getAllReviews(Long companyId) {
        GetCompanyResponseForReview company = externalCompanyService.getCompanyResponseForReview(companyId);

        List<Review> reviews = reviewRepository.findByCompanyId(companyId);
        Double companyRating = reviews.stream()
                .mapToDouble(Review::getRating)
                .average()
                .orElse(0.0);


        List<GetReviewResponse> reviewResponses = reviews.stream().map(reviewMapper::mapToResponse).toList();

        return GetAllReviewsByCompanyIdResponseDto.builder()
                .reviewResponses(reviewResponses)
                .companyName(company.companyName())
                .companyRating(companyRating)
                .build();
    }

    @Override
    @Transactional
    public GetReviewResponse addReview(Long companyId, CreateReviewRequestDto reviewDto) {
        checkCompanyExists(companyId);

        Review review = reviewMapper.mapFromCreateRequest(reviewDto);
        review.setCompanyId(companyId);

        reviewRepository.save(review);

        evictCacheAfterCommit(null, companyId);
        //TODO: will be outbox
        reviewMessageProducer.sendMessage(companyId);

        return reviewMapper.mapToResponse(review);
    }

    @Override
    @Cacheable(value = "reviews", key = "#reviewId", unless = "#result == null")
    @Transactional(readOnly = true)
    public GetReviewResponse getReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("Review given ID-" + reviewId + " not found!"));

        return reviewMapper.mapToResponse(review);
    }

    @Override
    @Transactional
    public GetReviewResponse updateReview(Long reviewId, UpdateReviewRequestDto updatedReview) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(() ->
                new ReviewNotFoundException("Review given by ID-" + reviewId + " not found!"));

        reviewMapper.mapForUpdateRequest(review, updatedReview);
        reviewRepository.save(review);

        evictCacheAfterCommit(reviewId, review.getCompanyId());

        //TODO: will be outbox
        reviewMessageProducer.sendMessage(review.getCompanyId());

        return reviewMapper.mapToResponse(review);
    }

    @Override
    @Transactional
    public void deleteReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(() ->
                new ReviewNotFoundException("Review given by ID-" + reviewId + " not found!"));

        reviewRepository.delete(review);
        evictCacheAfterCommit(reviewId, review.getCompanyId());

        //TODO: will be outbox
        reviewMessageProducer.sendMessage(review.getCompanyId());
    }

    @Override
    public GetAllReviewsForJobService getAllReviewsForJobService(Long companyId) {
        List<Review> reviews = reviewRepository.findByCompanyId(companyId);
        return reviewMapper.mapToGetAllReviewsForJobService(reviews);
    }

    @Override
    public Double getAverageRating(Long companyId) {
        return reviewRepository.findByCompanyId(companyId)
                .stream()
                .mapToDouble(Review::getRating)
                .average()
                .orElse(0.0);
    }

    private void checkCompanyExists(Long companyId) {
        if (!externalCompanyService.existsCompanyForReview(companyId))
            throw new CompanyNotFoundException("Company given by ID-" + companyId + " not found!");
    }

    private void evictCacheAfterCommit(Long reviewId, Long companyId) {
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            log.info("Transaction committed successfully. Clearing caches for company ID: {}", reviewId);
                            clearCaches(reviewId, companyId);
                        }
                    }
            );
        } else {
            log.info("No active transaction found. Direct cache clearing for company ID: {}", reviewId);
            clearCaches(reviewId, companyId);
        }
    }

    private void clearCaches(Long reviewId, Long companyId) {
        if (reviewId != null) {
            Cache reviewsCache = cacheManager.getCache("reviews");
            if (reviewsCache != null) {
                reviewsCache.evict(reviewId);
                log.debug("Evicted 'reviews' cache entry for ID: {}", reviewId);
            }
        }
        if (companyId != null) {
            Cache allReviewsCache = cacheManager.getCache("allReviews");
            if (allReviewsCache != null) {
                allReviewsCache.evict(companyId);
                log.debug("Cleared 'allReviews' cache");
            }
        }
    }
}