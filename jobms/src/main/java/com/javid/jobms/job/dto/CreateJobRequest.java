package com.javid.jobms.job.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateJobRequest (
    @NotBlank(message = "Title is required")
    @Size(max = 100)
    String title,
    String description,
    Double minSalary,
    Double maxSalary,
    String location,
    Long companyId
    ){

}
