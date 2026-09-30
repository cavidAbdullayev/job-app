package com.javid.reviewms.review.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CacheServiceImpl {
    private final CacheManager cacheManager;

    public void clearReviewCache() {
        evictCacheByName("reviews");
        evictCacheByName("allReviews");
    }

    public void clearAllCaches() {
        cacheManager.getCacheNames().forEach(this::evictCacheByName);
    }

    private void evictCacheByName(String name) {
        Cache cache = cacheManager.getCache(name);
        if (cache != null) {
            cache.clear();
            log.info("CACHE CLEARED: " + name);
        }
    }
}