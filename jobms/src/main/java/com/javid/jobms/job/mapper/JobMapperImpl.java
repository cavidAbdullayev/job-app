package com.javid.jobms.job.mapper;

import com.javid.jobms.job.dto.*;
import com.javid.jobms.job.entity.Job;
import com.javid.jobms.job.external.dto.Company;
import com.javid.jobms.job.external.dto.GetAllReviewsForJobService;
import com.javid.jobms.job.external.dto.GetReviewForJobService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobMapperImpl implements JobMapper {
    public GetJobResponse mapToJobDTO(Job job, Company company, GetAllReviewsForJobService reviews) {
        return GetJobResponse.builder()
                .title(job.getTitle())
                .description(job.getDescription())
                .minSalary(job.getMinSalary())
                .maxSalary(job.getMaxSalary())
                .location(job.getLocation())
                .companyId(job.getCompanyId())
                .company(mapToCompanyResponse(company))
                .review(mapToReviewResponseList(reviews))
                .build();
    }

    @Override
    public Job mapFromCreateRequest(CreateJobRequest createJobRequest) {
        return Job.builder()
                .description(createJobRequest.description())
                .title(createJobRequest.title())
                .minSalary(createJobRequest.minSalary())
                .maxSalary(createJobRequest.maxSalary())
                .location(createJobRequest.location())
                .companyId(createJobRequest.companyId())
                .build();
    }

    @Override
    public void mapForUpdate(Job job, UpdateJobRequest updatedJob) {
        if (updatedJob.title() != null)
            job.setTitle(updatedJob.title());
        if (updatedJob.description() != null)
            job.setDescription(updatedJob.description());
        if (updatedJob.minSalary() != null)
            job.setMinSalary(updatedJob.minSalary());
        if (updatedJob.maxSalary() != null)
            job.setMaxSalary(updatedJob.maxSalary());
        if (updatedJob.location() != null)
            job.setLocation(updatedJob.location());
    }

    private static CompanyResponse mapToCompanyResponse(Company company) {
        return CompanyResponse.builder()
                .name(company.getName())
                .description(company.getDescription())
                .build();
    }

    private List<ReviewResponse> mapToReviewResponseList(GetAllReviewsForJobService reviews) {
        return mapToReviewResponseList(reviews.reviews());
    }

    private List<ReviewResponse> mapToReviewResponseList(List<GetReviewForJobService> reviews) {
        return reviews.stream().map(this::mapToReviewResponse).toList();
    }

    private ReviewResponse mapToReviewResponse(GetReviewForJobService review) {
        return ReviewResponse.builder()
                .title(review.title())
                .description(review.description())
                .rating(review.rating())
                .build();
    }
}