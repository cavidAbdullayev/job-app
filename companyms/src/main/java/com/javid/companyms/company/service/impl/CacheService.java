package com.javid.companyms.company.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CacheService {

    private final CacheManager cacheManager;

    public CacheService(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public void clearCompanyCaches() {
        evictCacheByName("companies");
        evictCacheByName("allCompanies");
    }

    public void clearAllCaches() {
        cacheManager.getCacheNames()
                .forEach(this::evictCacheByName);
    }

    private void evictCacheByName(String cacheName) {
        var cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.clear();
            log.info("CACHE CLEARED: " + cacheName);
        }
    }
}