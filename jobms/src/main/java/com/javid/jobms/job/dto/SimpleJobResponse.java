package com.javid.jobms.job.dto;

import lombok.Builder;

@Builder
public record SimpleJobResponse(
        String title,
        String description,
        Long companyId,
        String location,
        Double maxSalary,
        Double minSalary

) {

}
