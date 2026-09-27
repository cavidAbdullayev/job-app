package com.javid.jobms.job.service.impl;

import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@Service
public class CacheService {

    private final CacheManager cacheManager;

    public CacheService(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public void clearCompanyCaches() {
        evictCacheByName("jobs");
        evictCacheByName("allJobs");
    }

    public void clearAllCaches() {
        cacheManager.getCacheNames()
                .forEach(this::evictCacheByName);
    }

    private void evictCacheByName(String cacheName) {
        var cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.clear();
            System.out.println("CACHE CLEARED: " + cacheName);
        }
    }
}