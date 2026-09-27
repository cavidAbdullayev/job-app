package com.javid.jobms.job.repository;

import com.javid.jobms.job.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository extends JpaRepository<Job, Long> {

}

