package com.javid.reviewms.review.repository;

import com.javid.reviewms.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Query("select r from Review r where r.companyId = :companyId")
    List<Review> findByCompanyId(@Param("companyId") Long companyId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.companyId = :companyId")
    Double getAvgRatingByCompanyId(@Param("companyId") Long companyId);
}
