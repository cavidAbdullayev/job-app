package com.javid.reviewms.review.external.service;

import com.javid.reviewms.review.clients.CompanyClient;
import com.javid.reviewms.review.external.dto.GetCompanyResponseForReview;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExternalCompanyService {

    private final CompanyClient companyClient;

    @Retry(name = "companyRetry", fallbackMethod = "getCompanyResponseForReviewFallback")
    @CircuitBreaker(name = "companyBreaker", fallbackMethod = "getCompanyResponseForReviewFallback")
    public GetCompanyResponseForReview getCompanyResponseForReview(Long companyId) {
        return companyClient.getCompanyResponseForReview(companyId);
    }

    @Retry(name = "companyRetry", fallbackMethod = "existsCompanyForReviewFallback")
    @CircuitBreaker(name = "companyBreaker", fallbackMethod = "existsCompanyForReviewFallback")
    public boolean existsCompanyForReview(Long companyId) {
        return companyClient.existsCompanyForReview(companyId);
    }

    public GetCompanyResponseForReview getCompanyResponseForReviewFallback(Long id, Throwable e) {
        log.error("Company service çağırışı uğursuz oldu (Company ID: {}): {}", id, e.getMessage());
        return new GetCompanyResponseForReview("Dummy: " + e.getMessage());
    }

    public boolean existsCompanyForReviewFallback(Long id, Throwable e) {
        log.error("Company service çağırışı uğursuz oldu (Company ID: {}): {}", id, e.getMessage());
        return false;
    }
}