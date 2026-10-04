package com.javid.emailservice.service;

import java.util.UUID;

public interface FailedEventRetryService {
    void retryFailedEvent(UUID failedEventId);
}
