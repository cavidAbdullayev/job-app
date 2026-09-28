package com.javid.reviewms.review.external.service;

import com.javid.reviewms.review.clients.CompanyClient;
import com.javid.reviewms.review.external.dto.GetCompanyResponseForReview;
import com.javid.reviewms.review.exception.CompanyNotFoundException;
import com.javid.reviewms.review.exception.InvalidInputException;
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

    public GetCompanyResponseForReview getCompanyResponseForReviewFallback(Long id, Throwable e) throws Throwable {
        if (e instanceof CompanyNotFoundException || e instanceof InvalidInputException) {
            throw e;
        }

        log.error("Company service is DOWN or timed out for ID {}. Triggering fallback. Error: {}", id, e.getMessage());

        return new GetCompanyResponseForReview("Company Name Unavailable (Service Temporary Down)");
    }

    public boolean existsCompanyForReviewFallback(Long id, Throwable e) throws Throwable {
        if (e instanceof CompanyNotFoundException || e instanceof InvalidInputException) {
            throw e;
        }

        log.error("Company service is DOWN for exists check ID {}. Error: {}", id, e.getMessage());
        return false;
    }
}