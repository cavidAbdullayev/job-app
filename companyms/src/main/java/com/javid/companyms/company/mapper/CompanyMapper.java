package com.javid.companyms.company.mapper;

import com.javid.companyms.company.dto.CreateCompanyRequest;
import com.javid.companyms.company.dto.GetCompanyResponse;
import com.javid.companyms.company.dto.GetCompanyResponseForReview;
import com.javid.companyms.company.dto.UpdateCompanyRequest;
import com.javid.companyms.company.entity.Company;

public interface CompanyMapper {
    GetCompanyResponse mapToResponse(Company company);
    Company mapCreateRequestToEntity(CreateCompanyRequest companyRequest);
    void mapForUpdate(Company company, UpdateCompanyRequest companyRequest);
}
