package com.javid.jobms.job.dto;

public record CreateJobRequest (
    String title,
    String description,
    Double minSalary,
    Double maxSalary,
    String location,
    Long companyId
    ){

}
