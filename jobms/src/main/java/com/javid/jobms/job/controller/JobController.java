package com.javid.jobms.job.controller;


import com.javid.jobms.job.dto.CreateJobRequest;
import com.javid.jobms.job.dto.GetJobResponse;
import com.javid.jobms.job.dto.UpdateJobRequest;
import com.javid.jobms.job.service.JobService;
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
    public ResponseEntity<String> createJob(@RequestBody CreateJobRequest jobRequest) {
        boolean isCreated = jobService.createJob(jobRequest);
        if(isCreated)
            return new ResponseEntity<>("Job added successfully!", HttpStatus.CREATED);
        return new ResponseEntity<>("Job couldn't be added!", HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<GetJobResponse> getJobById(@PathVariable("id") Long id) {
        GetJobResponse job = jobService.getJobById(id);
        if (job != null)
            return new ResponseEntity<>(job, HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteJob(@PathVariable("id") Long id) {
        boolean deleted = jobService.deleteJobById(id);
        if (deleted)
            return new ResponseEntity<>("Job deleted successfully", HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<String> updateJob(@PathVariable("id") Long id,
                                            @RequestBody UpdateJobRequest updatedJob) {
        boolean updated = jobService.updateJob(id, updatedJob);
        if (updated)
            return new ResponseEntity<>("Job updated successfully", HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}