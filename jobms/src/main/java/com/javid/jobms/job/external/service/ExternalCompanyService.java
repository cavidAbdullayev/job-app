package com.javid.jobms.job.external.service;


import com.javid.jobms.job.clients.CompanyClient;
import com.javid.jobms.job.external.dto.Company;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExternalCompanyService {

    private final CompanyClient companyClient;
    @Retry(name = "companyRetry", fallbackMethod = "companyBreakerFallback")
    @CircuitBreaker(name = "companyBreaker", fallbackMethod = "companyBreakerFallback")
    public Company getCompany(Long id) {
        return companyClient.getCompany(id);
    }

    @Retry(name = "companyRetry", fallbackMethod = "existsCompanyForReviewFallback")
    @CircuitBreaker(name = "companyBreaker", fallbackMethod = "existsCompanyForReviewFallback")
    public boolean existsCompanyForReview(Long companyId){
        return companyClient.existsCompanyForReview(companyId);
    }

    public Company companyBreakerFallback(Long id, Throwable e) {
        log.error("Company service çağırışı uğursuz oldu (Company ID: {}): {}", id, e.getMessage());
        Company dummyCompany = new Company();
        dummyCompany.setName("Dummy: " + e.getMessage());
        Objects.equals(1,2);
        return dummyCompany;
    }

    public boolean existsCompanyForReviewFallback(Long id, Throwable e) {
        log.error("Company service çağırışı uğursuz oldu (Company ID: {}): {}", id, e.getMessage());
        return false;
    }
}