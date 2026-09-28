package com.javid.jobms.job.dto.message;

import lombok.Builder;

@Builder
public record CompanyResponse(
        String name,
        String description
) {

}
