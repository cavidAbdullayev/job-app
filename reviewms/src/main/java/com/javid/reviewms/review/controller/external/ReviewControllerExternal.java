package com.javid.reviewms.review.controller.external;

import com.javid.reviewms.review.service.ReviewService;
import com.javid.reviewms.review.dto.CreateReviewRequestDto;
import com.javid.reviewms.review.dto.GetAllReviewsByCompanyIdResponseDto;
import com.javid.reviewms.review.dto.GetReviewResponse;
import com.javid.reviewms.review.dto.UpdateReviewRequestDto;
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
    public ResponseEntity<String> addReview(@RequestParam Long companyId,
                                            @RequestBody CreateReviewRequestDto reviewDto
    ) {
        boolean isCreated = reviewService.addReview(companyId, reviewDto);
        if (isCreated)
            return new ResponseEntity<>("Review added successfully", HttpStatus.OK);

        return new ResponseEntity<>("Review not saved", HttpStatus.NOT_FOUND);
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<GetReviewResponse> getReview(@PathVariable Long reviewId) {
        GetReviewResponse reviewResponse = reviewService.getReview(reviewId);
        if(reviewResponse != null)
            return new ResponseEntity<>(
                    reviewResponse,
                    HttpStatus.OK
            );
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<String> updateReview(@PathVariable Long reviewId,
                                               @RequestBody UpdateReviewRequestDto updatedReview) {

        boolean isSaved = reviewService.updateReview(reviewId, updatedReview);
        if (isSaved)
            return new ResponseEntity<>("Review updated successfully", HttpStatus.OK);

        return new ResponseEntity<>("Review not updated", HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<String> deleteReview(@PathVariable Long reviewId) {
        boolean isReviewDeleted = reviewService.deleteReview(reviewId);

        if (isReviewDeleted)
            return new ResponseEntity<>("Review deleted successfully", HttpStatus.OK);

        return new ResponseEntity<>("Review not deleted", HttpStatus.NOT_FOUND);

    }

    @GetMapping("/averageRating")
    public Double getAverageReview(@RequestParam Long companyId){
        return reviewService
                .getAllReviews(companyId)
                .reviewResponses()
                .stream()
                .mapToDouble(GetReviewResponse::rating)
                .average()
                .orElse(0.0);
    }

}