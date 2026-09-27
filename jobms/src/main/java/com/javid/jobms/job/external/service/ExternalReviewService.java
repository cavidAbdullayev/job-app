package com.javid.jobms.job.external.service;

import com.javid.jobms.job.clients.ReviewClient;
import com.javid.jobms.job.external.dto.GetAllReviewsForJobService;
import com.javid.jobms.job.external.dto.GetReviewForJobService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExternalReviewService {

    private final ReviewClient reviewClient;

    @Retry(name = "reviewRetry", fallbackMethod = "reviewBreakerFallback")
    @CircuitBreaker(name = "reviewBreaker")
    public GetAllReviewsForJobService getReviews(Long companyId) {
        ResponseEntity<GetAllReviewsForJobService> response = reviewClient.getReview(companyId);
        return (response != null && response.getBody() != null) ? response.getBody() : GetAllReviewsForJobService.builder().reviews(Collections.emptyList()).build();
    }

    public List<GetAllReviewsForJobService> reviewBreakerFallback(Long companyId, Exception e) {
        log.error("Review service çağırışı uğursuz oldu (Company ID: {}): {}", companyId, e.getMessage());
        GetAllReviewsForJobService review = GetAllReviewsForJobService.builder()
                .reviews(
                        List.of(
                                GetReviewForJobService.builder()
                                        .title("Dummy: " + e.getMessage())
                                        .build())
                )
                .build();
        return List.of(review);
    }
}