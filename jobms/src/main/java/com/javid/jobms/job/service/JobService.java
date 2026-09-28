package com.javid.jobms.job.service;


import com.javid.jobms.job.dto.CreateJobRequest;
import com.javid.jobms.job.dto.GetJobResponse;
import com.javid.jobms.job.dto.SimpleJobResponse;
import com.javid.jobms.job.dto.UpdateJobRequest;

import java.util.List;

public interface JobService {
    List<GetJobResponse> findAll();
    SimpleJobResponse createJob(CreateJobRequest jobRequest);

    GetJobResponse getJobById(Long id);

    void deleteJobById(Long id);

    SimpleJobResponse updateJob(Long id, UpdateJobRequest updatedJob);
}
