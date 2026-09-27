package com.javid.reviewms.review.clients;

import com.javid.reviewms.review.external.dto.GetCompanyResponseForReview;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("company-service")
public interface CompanyClient {
    @GetMapping("internal/companies/get-company-response-for-review/{companyId}")
    GetCompanyResponseForReview getCompanyResponseForReview(@PathVariable Long companyId);

    @GetMapping("internal/companies/is-exists-company-for-review-service/{companyId}")
    boolean existsCompanyForReview(@PathVariable Long companyId);
}
