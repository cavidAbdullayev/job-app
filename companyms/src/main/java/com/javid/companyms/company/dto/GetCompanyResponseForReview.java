package com.javid.companyms.company.dto;

import lombok.Builder;

@Builder
public record GetCompanyResponseForReview(
        String companyName
) {

}
