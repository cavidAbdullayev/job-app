package com.javid.reviewms.review.controller.internal;

import com.javid.reviewms.review.external.dto.GetAllReviewsForJobService;
import com.javid.reviewms.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/reviews")
@RequiredArgsConstructor
public class ReviewControllerInternal {
    private final ReviewService reviewService;

    @GetMapping ("/get-all-job-service/{companyId}")
    public GetAllReviewsForJobService getAllReviewsForJobService(@PathVariable("companyId") Long companyId){
        return reviewService.getAllReviewsForJobService(companyId);
    }

}
