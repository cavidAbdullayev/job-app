package com.javid.reviewms.review.controller.internal;

import com.javid.reviewms.review.service.impl.CacheServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cache")
@RequiredArgsConstructor
public class CacheController {
    private final CacheServiceImpl cacheService;

    @DeleteMapping("/review")
    public ResponseEntity<String> clearCompanyCaches() {
        cacheService.clearReviewCache();
        return ResponseEntity.ok("Review caches cleared");
    }

    @DeleteMapping("/all")
    public ResponseEntity<String> clearAllCaches() {
        cacheService.clearAllCaches();
        return ResponseEntity.ok("All caches cleared");
    }
}