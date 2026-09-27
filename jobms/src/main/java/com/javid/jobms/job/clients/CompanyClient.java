package com.javid.jobms.job.clients;

import com.javid.jobms.job.external.dto.Company;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "company-service")
public interface CompanyClient {

    @GetMapping("external/companies/{id}")
    Company getCompany(@PathVariable Long id);

    @GetMapping("internal/companies/is-exists-company-for-review-service/{companyId}")
    boolean existsCompanyForReview(@PathVariable Long companyId);
}
