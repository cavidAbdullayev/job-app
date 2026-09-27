package com.javid.companyms.company.mapper.impl;

import com.javid.companyms.company.dto.CreateCompanyRequest;
import com.javid.companyms.company.dto.GetCompanyResponse;
import com.javid.companyms.company.dto.UpdateCompanyRequest;
import com.javid.companyms.company.entity.Company;
import com.javid.companyms.company.mapper.CompanyMapper;
import org.springframework.stereotype.Service;

@Service
public class CompanyMapperImpl implements CompanyMapper {
    @Override
    public GetCompanyResponse mapToResponse(Company company) {
        return GetCompanyResponse.builder()
                .name(company.getName())
                .description(company.getDescription())
                .rating(company.getRating())
                .build();
    }

    @Override
    public Company mapCreateRequestToEntity(CreateCompanyRequest companyRequest) {
        return Company.builder()
                .description(companyRequest.description())
                .name(companyRequest.name())
                .rating(0.0)
                .build();
    }

    @Override
    public void mapForUpdate(Company company, UpdateCompanyRequest companyRequest) {
        if(companyRequest.description() != null)
            company.setDescription(companyRequest.description());
        if(companyRequest.name() != null)
            company.setName(companyRequest.name());
    }


}
