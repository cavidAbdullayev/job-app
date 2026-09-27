package com.javid.companyms.company.controller.internal;

import com.javid.companyms.company.dto.GetCompanyResponseForReview;
import com.javid.companyms.company.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/companies")
public class InternalCompanyController {
    private final CompanyService companyService;

    @GetMapping("/get-company-response-for-review/{companyId}")
    public GetCompanyResponseForReview getCompanyResponseForReview(@PathVariable Long companyId) {
        return companyService.getCompanyResponseForReview(companyId);
    }

    @GetMapping("/is-exists-company-for-review-service/{companyId}")
    public boolean existsCompanyForReview(@PathVariable Long companyId){
        return companyService.existsCompanyForReview(companyId);
    }
}
