package com.javid.userservice.service;

import com.javid.userservice.entity.OutboxEntity;

public interface OutboxProcessorService {
    void processSingleOutbox(OutboxEntity outbox);
}
