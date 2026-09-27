package com.javid.jobms.job.service.impl;


import com.javid.jobms.job.dto.CreateJobRequest;
import com.javid.jobms.job.dto.GetJobResponse;
import com.javid.jobms.job.dto.UpdateJobRequest;
import com.javid.jobms.job.entity.Job;
import com.javid.jobms.job.external.dto.Company;
import com.javid.jobms.job.external.dto.GetAllReviewsForJobService;
import com.javid.jobms.job.external.service.ExternalCompanyService;
import com.javid.jobms.job.external.service.ExternalReviewService;
import com.javid.jobms.job.mapper.JobMapper;
import com.javid.jobms.job.repository.JobRepository;
import com.javid.jobms.job.service.JobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {
    private final JobRepository jobRepository;
    private final ExternalCompanyService externalCompanyService;
    private final ExternalReviewService externalReviewService;
    private final JobMapper jobMapper;


    @Override
    @Cacheable(value = "allJobs")
    public List<GetJobResponse> findAll() {
        log.info("find all jobs method started");
        List<Job> jobs = jobRepository.findAll();
        log.info("all jobs retrieved");
        return jobs.stream()
                .map(this::convertToDto)
                .toList();
    }


    @Override
    @CacheEvict(value = "allJobs", allEntries = true)
    public boolean createJob(CreateJobRequest jobRequest) {
        if(jobRequest != null && jobRequest.companyId() != null) {
            Long companyId = jobRequest.companyId();
            boolean existsCompany = externalCompanyService.existsCompanyForReview(companyId);
            if(!existsCompany){
                //TODO: throw ex
                return false;
            }

            Job job = jobMapper.mapFromCreateRequest(jobRequest);
            jobRepository.save(job);

            return true;
        }
        return false;
    }

    @Override
    @Cacheable(value = "jobs", key = "#id")
    public GetJobResponse getJobById(Long id) {
        return convertToDto(jobRepository.findById(id).orElse(null));
    }

    @Override
    @Caching(
            evict = {
                    @CacheEvict(value = "allJobs", allEntries = true),
                    @CacheEvict(value = "jobs", key = "#id")
            }
    )
    public boolean deleteJobById(Long id) {
        if (jobRepository.existsById(id)) {
            jobRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    @Caching(
            evict = {
                    @CacheEvict(value = "allJobs", allEntries = true),
                    @CacheEvict(value = "jobs", key = "#id")
            }
    )
    public boolean updateJob(Long id, UpdateJobRequest updatedJob) {
        Optional<Job> jobOptional = jobRepository.findById(id);
        if (jobOptional.isPresent()) {
            Job job = jobOptional.get();
            jobMapper.mapForUpdate(job, updatedJob);
            jobRepository.save(job);

            return true;
        }
        return false;
    }

    private GetJobResponse convertToDto(Job job) {
        log.info("convertToDto method started for company id: {}", job.getCompanyId());
        Company company = externalCompanyService.getCompany(job.getCompanyId());
        GetAllReviewsForJobService reviews = externalReviewService.getReviews(job.getCompanyId());

        return jobMapper.mapToJobDTO(job, company, reviews);
    }
}