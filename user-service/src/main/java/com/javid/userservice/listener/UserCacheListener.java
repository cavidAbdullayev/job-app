package com.javid.userservice.listener;

import com.javid.userservice.event.UserCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.cache.CacheException;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserCacheListener {

    private final CacheManager cacheManager;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Retryable(retryFor = {CacheException.class},
            backoff = @Backoff(delay = 1000, multiplier = 2))
    public void handleUserCreatedEvent(UserCreatedEvent userCreatedEvent) {
        log.debug("Processing cache eviction for event: {}", userCreatedEvent);
        String userId = userCreatedEvent.userId();

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

    @Recover
    public void recover(Exception e, UserCreatedEvent event) {
        log.error("All 3 retry attempts failed to clear cache for user ID: {}. Error: {}",
                event.userId(), e.getMessage());
    }
}