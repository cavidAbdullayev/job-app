package com.javid.companyms.company.service;

import com.javid.companyms.company.dto.CreateCompanyRequest;
import com.javid.companyms.company.dto.GetCompanyResponse;
import com.javid.companyms.company.dto.GetCompanyResponseForReview;
import com.javid.companyms.company.dto.UpdateCompanyRequest;
import com.javid.companyms.company.entity.Company;
import com.javid.companyms.company.dto.messaging.ReviewMessage;

import java.util.List;

public interface CompanyService {
    List<GetCompanyResponse> getAllCompanies();
    GetCompanyResponse updateCompany(UpdateCompanyRequest updatedCompany, Long id);
    GetCompanyResponse createCompany(CreateCompanyRequest companyRequest);
    void deleteCompanyById(Long id);

    GetCompanyResponse getCompanyById(Long id);
    void updateCompanyRating(ReviewMessage reviewMessage);
    GetCompanyResponseForReview getCompanyResponseForReview(Long companyId);

    boolean existsCompanyForReview(Long companyId);
}
