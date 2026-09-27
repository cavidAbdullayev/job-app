package com.javid.jobms.job.service;


import com.javid.jobms.job.dto.CreateJobRequest;
import com.javid.jobms.job.dto.GetJobResponse;
import com.javid.jobms.job.dto.UpdateJobRequest;
import com.javid.jobms.job.entity.Job;

import java.util.List;

public interface JobService {
    List<GetJobResponse> findAll();
    boolean createJob(CreateJobRequest jobRequest);

    GetJobResponse getJobById(Long id);

    boolean deleteJobById(Long id);

    boolean updateJob(Long id, UpdateJobRequest updatedJob);
}
