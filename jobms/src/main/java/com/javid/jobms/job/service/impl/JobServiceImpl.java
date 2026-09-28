package com.javid.jobms.job.service.impl;

import com.javid.jobms.job.dto.CreateJobRequest;
import com.javid.jobms.job.dto.GetJobResponse;
import com.javid.jobms.job.dto.SimpleJobResponse;
import com.javid.jobms.job.dto.UpdateJobRequest;
import com.javid.jobms.job.entity.Job;
import com.javid.jobms.job.exception.CompanyNotFoundException;
import com.javid.jobms.job.exception.JobNotFoundException;
import com.javid.jobms.job.external.dto.Company;
import com.javid.jobms.job.external.dto.GetAllReviewsForJobService;
import com.javid.jobms.job.external.service.ExternalCompanyService;
import com.javid.jobms.job.external.service.ExternalReviewService;
import com.javid.jobms.job.mapper.JobMapper;
import com.javid.jobms.job.repository.JobRepository;
import com.javid.jobms.job.service.JobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;
    private final ExternalCompanyService externalCompanyService;
    private final ExternalReviewService externalReviewService;
    private final JobMapper jobMapper;
    private final CacheManager cacheManager;

    @Override
    @Cacheable(value = "allJobs")
    @Transactional(readOnly = true)
    public List<GetJobResponse> findAll() {
        log.info("Fetching all jobs from database");
        List<Job> jobs = jobRepository.findAll();
        log.debug("Found {} jobs in repository", jobs.size());

        return jobs.stream()
                .map(this::convertToDto)
                .toList();
    }

    @Override
    @Transactional
    public SimpleJobResponse createJob(CreateJobRequest jobRequest) {
        Long companyId = jobRequest.companyId();
        log.info("Creating a new job for company ID: {}", companyId);

        boolean existsCompany = externalCompanyService.existsCompanyForReview(companyId);
        if (!existsCompany) {
            log.error("Failed to create job: Company with ID {} does not exist", companyId);
            throw new CompanyNotFoundException("Company given by ID-" + companyId + " not found");
        }

        Job job = jobMapper.mapFromCreateRequest(jobRequest);
        Job savedJob = jobRepository.save(job);
        log.info("Successfully created job with ID: {}", savedJob.getId());

        evictCacheAfterCommit(job.getId());

        return jobMapper.mapToSimpleJobResponse(savedJob);
    }

    @Override
    @Cacheable(value = "jobs", key = "#id")
    @Transactional(readOnly = true)
    public GetJobResponse getJobById(Long id) {
        log.info("Fetching job details for job ID: {}", id);

        Job job = jobRepository.findById(id).orElseThrow(() -> {
            log.error("Job not found with ID: {}", id);
            return new JobNotFoundException("Job given by id-" + id + " not found!");
        });

        return convertToDto(job);
    }

    @Override
    @Transactional
    public void deleteJobById(Long id) {
        log.info("Deleting job with ID: {}", id);

        Job job = jobRepository.findById(id).orElseThrow(() -> {
            log.error("Failed to delete: Job not found with ID: {}", id);
            return new JobNotFoundException("Job given by id-" + id + " not found!");
        });

        jobRepository.delete(job);

        evictCacheAfterCommit(id);
        log.info("Successfully deleted job with ID: {}", id);
    }

    @Override
    @Transactional
    public SimpleJobResponse updateJob(Long id, UpdateJobRequest updatedJob) {
        log.info("Updating job with ID: {}", id);

        Job job = jobRepository.findById(id).orElseThrow(() -> {
            log.error("Failed to update: Job not found with ID: {}", id);
            return new JobNotFoundException("Job given by id-" + id + " not found!");
        });

        jobMapper.mapForUpdate(job, updatedJob);
        Job savedJob = jobRepository.save(job);
        log.info("Successfully updated job with ID: {}", id);

        evictCacheAfterCommit(id);

        return jobMapper.mapToSimpleJobResponse(savedJob);
    }

    private GetJobResponse convertToDto(Job job) {
        log.debug("Fetching external details (company and reviews) for job ID: {} and company ID: {}",
                job.getId(), job.getCompanyId());

        Company company = externalCompanyService.getCompany(job.getCompanyId());
        GetAllReviewsForJobService reviews = externalReviewService.getReviews(job.getCompanyId());

        return jobMapper.mapToJobDTO(job, company, reviews);
    }

    private void evictCacheAfterCommit(Long companyId) {
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            log.info("Transaction committed successfully. Clearing caches for company ID: {}", companyId);
                            clearCaches(companyId);
                        }
                    }
            );
        } else {
            log.info("No active transaction found. Direct cache clearing for company ID: {}", companyId);
            clearCaches(companyId);
        }
    }

    private void clearCaches(Long companyId) {
        if (companyId != null) {
            Cache jobCache = cacheManager.getCache("jobs");
            if (jobCache != null) {
                jobCache.evict(companyId);
                log.debug("Evicted 'jobs' cache entry for ID: {}", companyId);
            }

            Cache allJobssCache = cacheManager.getCache("allJobs");
            if (allJobssCache != null) {
                allJobssCache.clear();
                log.debug("Cleared 'allJobs' cache");
            }
        }
    }
}