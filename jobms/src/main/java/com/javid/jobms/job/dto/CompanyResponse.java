package com.javid.jobms.job.dto;

import lombok.Builder;

@Builder
public record CompanyResponse(
        String name,
        String description
) {

}
