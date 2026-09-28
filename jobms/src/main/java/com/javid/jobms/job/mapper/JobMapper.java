package com.javid.jobms.job.mapper;

import com.javid.jobms.job.dto.CreateJobRequest;
import com.javid.jobms.job.dto.GetJobResponse;
import com.javid.jobms.job.dto.SimpleJobResponse;
import com.javid.jobms.job.dto.UpdateJobRequest;
import com.javid.jobms.job.entity.Job;
import com.javid.jobms.job.external.dto.Company;
import com.javid.jobms.job.external.dto.GetAllReviewsForJobService;

import java.util.List;

public interface JobMapper {
    GetJobResponse mapToJobDTO(Job job, Company company, GetAllReviewsForJobService reviews);

    Job mapFromCreateRequest(CreateJobRequest createJobRequest);
    void mapForUpdate(Job job, UpdateJobRequest updatedJob);
    SimpleJobResponse mapToSimpleJobResponse(Job job);
}