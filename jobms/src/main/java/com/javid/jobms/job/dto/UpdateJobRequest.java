package com.javid.jobms.job.dto;

public record UpdateJobRequest(
        String title,
        String description,
        Double minSalary,
        Double maxSalary,
        String location
) {

}
