package com.javid.jobms.job.clients;

import com.javid.jobms.job.external.dto.GetAllReviewsForJobService;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "review-service")
public interface ReviewClient {
    @GetMapping("internal/reviews/get-all-job-service/{companyId}")
    ResponseEntity<GetAllReviewsForJobService> getReview(@PathVariable Long companyId);
}
