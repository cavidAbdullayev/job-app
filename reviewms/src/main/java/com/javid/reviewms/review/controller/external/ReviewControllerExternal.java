package com.javid.reviewms.review.controller.external;

import com.javid.reviewms.review.service.ReviewService;
import com.javid.reviewms.review.dto.CreateReviewRequestDto;
import com.javid.reviewms.review.dto.GetAllReviewsByCompanyIdResponseDto;
import com.javid.reviewms.review.dto.GetReviewResponse;
import com.javid.reviewms.review.dto.UpdateReviewRequestDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("external/reviews")
@RequiredArgsConstructor
public class ReviewControllerExternal {
    private final ReviewService reviewService;

    @GetMapping
    public ResponseEntity<GetAllReviewsByCompanyIdResponseDto> getAllReviews(@RequestParam Long companyId) {
        return new ResponseEntity<>(
                reviewService.getAllReviews(companyId),
                HttpStatus.OK
        );
    }

    @PostMapping
    public ResponseEntity<GetReviewResponse> addReview(@RequestParam Long companyId,
                                                       @RequestBody @Valid CreateReviewRequestDto reviewDto
    ) {
        return new ResponseEntity<>(reviewService.addReview(companyId, reviewDto), HttpStatus.CREATED);
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<GetReviewResponse> getReview(@PathVariable Long reviewId) {
        GetReviewResponse reviewResponse = reviewService.getReview(reviewId);
        if (reviewResponse != null)
            return new ResponseEntity<>(
                    reviewResponse,
                    HttpStatus.OK
            );
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<GetReviewResponse> updateReview(@PathVariable Long reviewId,
                                                          @RequestBody @Valid UpdateReviewRequestDto updatedReview) {

        return new ResponseEntity<>(reviewService.updateReview(reviewId, updatedReview), HttpStatus.OK);
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/averageRating")
    public ResponseEntity<Double> getAverageRating(@RequestParam Long companyId) {
        return new ResponseEntity<>(reviewService.getAverageRating(companyId), HttpStatus.OK);
    }

}