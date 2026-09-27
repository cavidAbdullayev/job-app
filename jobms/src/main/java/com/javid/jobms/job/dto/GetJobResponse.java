package com.javid.jobms.job.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetJobResponse {
    private String title;
    private String description;
    private Double minSalary;
    private Double maxSalary;
    private String location;
    private Long companyId;
    private CompanyResponse company;
    private List<ReviewResponse> review;
}
