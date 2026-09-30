package com.javid.userservice.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserCacheListener {

    private final CacheManager cacheManager;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void evictCache(String userId) {
        if (userId != null) {
            Cache cacheUser = cacheManager.getCache("user");
            if (cacheUser != null) {
                cacheUser.evict(userId);
                log.debug("Evicted 'user' cache entry for ID: {}", userId);
            }
        }

        Cache cacheAllUsers = cacheManager.getCache("allUsers");
        if (cacheAllUsers != null) {
            cacheAllUsers.clear();
            log.debug("Cleared 'allUsers' cache");
        }
    }

}