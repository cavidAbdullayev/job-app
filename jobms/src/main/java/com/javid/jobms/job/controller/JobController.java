package com.javid.jobms.job.controller;


import com.javid.jobms.job.dto.CreateJobRequest;
import com.javid.jobms.job.dto.GetJobResponse;
import com.javid.jobms.job.dto.SimpleJobResponse;
import com.javid.jobms.job.dto.UpdateJobRequest;
import com.javid.jobms.job.service.JobService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/jobs")
public class JobController {
    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @RequestMapping(value = "/job", method = RequestMethod.GET)
    public ResponseEntity<List<GetJobResponse>> findAll() {
        return ResponseEntity.ok(jobService.findAll());
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<SimpleJobResponse> createJob(@RequestBody @Valid CreateJobRequest jobRequest) {
        return ResponseEntity.ok(jobService.createJob(jobRequest));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<GetJobResponse> getJobById(@PathVariable("id") Long id) {
        GetJobResponse job = jobService.getJobById(id);
        if (job != null)
            return new ResponseEntity<>(job, HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteJob(@PathVariable("id") Long id) {
        jobService.deleteJobById(id);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<SimpleJobResponse> updateJob(@PathVariable("id") Long id,
                                                       @RequestBody UpdateJobRequest updatedJob) {
        return ResponseEntity.ok(jobService.updateJob(id, updatedJob));
    }
}