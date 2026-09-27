package com.javid.companyms.company.dto;

import lombok.Builder;

@Builder
public record GetCompanyResponse(
        String name,
        String description,
        Double rating
) {
}
